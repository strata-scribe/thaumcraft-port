package thaumcraft.common.items.curios;
import net.minecraft.world.item.Item;
public class DummyItem extends Item {
    public DummyItem(Properties p) { super(p); }
    @Override
    public net.minecraft.world.InteractionResult use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) { return null; }
}
