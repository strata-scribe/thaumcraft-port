package thaumcraft.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;

/**
 * Menu (container) for the Arcane Workbench.
 *
 * Slot layout (total = 1 + 9 + 6 + 27 + 9 = 52):
 * <pre>
 *  Index  Purpose
 *  -----  -----------------------------------------------------------
 *    0    Result slot (output only, read from ResultContainer)
 *   1-9   Crafting matrix (maps to BE slots 0-8)
 *  10-15  Crystal slots  (maps to BE slots 9-14)
 *  16-42  Player inventory (3 rows × 9)
 *  43-51  Player hotbar
 * </pre>
 */
public class ArcaneWorkbenchMenu extends AbstractContainerMenu {

    // -------------------------------------------------------------------------
    // Layout constants
    // -------------------------------------------------------------------------

    private static final int RESULT_INDEX   = 0;
    private static final int CRAFT_START    = 1;   // inclusive
    private static final int CRAFT_END      = 9;   // inclusive  (9 slots)
    private static final int CRYSTAL_START  = 10;  // inclusive
    private static final int CRYSTAL_END    = 15;  // inclusive  (6 slots)
    private static final int INV_START      = 16;  // inclusive
    private static final int INV_END        = 42;  // inclusive  (27 slots)
    private static final int HOTBAR_START   = 43;  // inclusive
    private static final int HOTBAR_END     = 51;  // inclusive  (9 slots)

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Dummy single-slot container used for the result slot. */
    private final ResultContainer resultContainer = new ResultContainer();

    /** The workbench block entity that owns the actual items. */
    private final ArcaneWorkbenchBlockEntity workbench;

    private final ContainerLevelAccess access;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Server-side constructor — called by {@link ArcaneWorkbenchBlockEntity#createMenu}. */
    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory,
                               ArcaneWorkbenchBlockEntity workbench,
                               ContainerLevelAccess access) {
        super(ThaumcraftMenus.ARCANE_WORKBENCH.get(), containerId);
        this.workbench = workbench;
        this.access    = access;

        addAllSlots(playerInventory);
    }

    /**
     * Client-side factory — called by NeoForge's IMenuTypeExtension when the
     * server sends the open-menu packet.  We look up the BE from the world.
     */
    public static ArcaneWorkbenchMenu createClientSide(int containerId,
                                                        Inventory playerInventory,
                                                        FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        // On the client level, try to fetch the real BE; fall back to a dummy.
        ArcaneWorkbenchBlockEntity be = null;
        if (playerInventory.player.level().getBlockEntity(pos) instanceof ArcaneWorkbenchBlockEntity found) {
            be = found;
        }
        if (be == null) {
            // Dummy BE so the client menu can at least open without NPE
            be = new ArcaneWorkbenchBlockEntity(pos,
                    playerInventory.player.level().getBlockState(pos));
        }
        return new ArcaneWorkbenchMenu(containerId, playerInventory, be,
                ContainerLevelAccess.create(playerInventory.player.level(), pos));
    }

    // -------------------------------------------------------------------------
    // Slot layout helper
    // -------------------------------------------------------------------------

    private void addAllSlots(Inventory playerInventory) {
        // -- Result slot (index 0) -------------------------------------------
        addSlot(new Slot(resultContainer, 0, 160, 64) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
            @Override
            public boolean mayPickup(Player player) { return true; }
        });

        // -- Crafting matrix (slots 0-8 in the BE → menu slots 1-9) ----------
        SimpleContainer craftProxy = makeProxy(workbench, 0, ArcaneWorkbenchBlockEntity.CRAFT_SLOTS);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(craftProxy, row * 3 + col, 40 + col * 24, 40 + row * 24));
            }
        }

        // -- Crystal slots (slots 9-14 in the BE → menu slots 10-15) ---------
        SimpleContainer crystalProxy = makeProxy(workbench,
                ArcaneWorkbenchBlockEntity.CRAFT_SLOTS,
                ArcaneWorkbenchBlockEntity.CRYSTAL_SLOTS);
        int[] crystalX = { 64, 17, 112, 17, 112, 64 };
        int[] crystalY = { 13, 35, 35, 93, 93, 115 };
        for (int i = 0; i < ArcaneWorkbenchBlockEntity.CRYSTAL_SLOTS; i++) {
            addSlot(new Slot(crystalProxy, i, crystalX[i], crystalY[i]));
        }

        // -- Player inventory (3 rows of 9) -----------------------------------
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, 9 + row * 9 + col,
                        16 + col * 18, 151 + row * 18));
            }
        }

        // -- Player hotbar ----------------------------------------------------
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 16 + col * 18, 209));
        }
    }

    // -------------------------------------------------------------------------
    // Proxy helper — wraps a sub-range of the BE's NonNullList as a Container
    // -------------------------------------------------------------------------

    /**
     * Returns a lightweight {@link SimpleContainer} view over a contiguous
     * slice of the workbench's item list, so each slot gets the correct index.
     */
    private static SimpleContainer makeProxy(ArcaneWorkbenchBlockEntity be, int offset, int size) {
        SimpleContainer proxy = new SimpleContainer(size) {
            @Override
            public ItemStack getItem(int slot) {
                return be.items.get(offset + slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                be.items.set(offset + slot, stack);
                be.setChanged();
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                ItemStack result = net.minecraft.world.ContainerHelper
                        .removeItem(be.items, offset + slot, amount);
                if (!result.isEmpty()) be.setChanged();
                return result;
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                ItemStack stack = be.items.get(offset + slot);
                be.items.set(offset + slot, ItemStack.EMPTY);
                return stack;
            }
        };
        return proxy;
    }

    // -------------------------------------------------------------------------
    // Standard overrides
    // -------------------------------------------------------------------------

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate(
                (level, pos) -> level.getBlockState(pos).is(ThaumcraftBlocks.arcaneWorkbench.get())
                        && player.distanceToSqr(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64.0,
                true);
    }

    /**
     * Shift-click handling.
     *
     * <ul>
     *   <li>Clicking a workbench slot (0-15) → try to put item into player inv/hotbar.</li>
     *   <li>Clicking the result slot (0)     → same.</li>
     *   <li>Clicking player inv (16-42)      → try craft grid, then crystals.</li>
     *   <li>Clicking hotbar (43-51)          → try inv, then craft, then crystals.</li>
     * </ul>
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (!slot.hasItem()) return remainder;

        ItemStack stack = slot.getItem();
        remainder = stack.copy();

        // -- Source: workbench slots (result + craft + crystal) → player inv --
        if (index <= CRYSTAL_END) {
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END + 1, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, remainder);

        } else {
            // -- Source: player hotbar → player inventory first ---------------
            if (index >= HOTBAR_START) {
                if (!moveItemStackTo(stack, INV_START, INV_END + 1, false)) {
                    // Fall through to craft grid below
                    if (!moveItemStackTo(stack, CRAFT_START, CRAFT_END + 1, false)) {
                        if (!moveItemStackTo(stack, CRYSTAL_START, CRYSTAL_END + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else {
                // -- Source: player inventory → hotbar first ------------------
                if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END + 1, false)) {
                    if (!moveItemStackTo(stack, CRAFT_START, CRAFT_END + 1, false)) {
                        if (!moveItemStackTo(stack, CRYSTAL_START, CRYSTAL_END + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == remainder.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        return remainder;
    }
}
