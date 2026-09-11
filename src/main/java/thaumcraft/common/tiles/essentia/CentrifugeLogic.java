package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.serialization.Codec;
import thaumcraft.api.aspects.Aspect;

public class CentrifugeLogic {

    private final Runnable setChanged;

    public Aspect aspectIn = null;
    public int amountIn = 0;
    public final int maxIn = 16;

    public Aspect aspectOut1 = null;
    public int amountOut1 = 0;
    public Aspect aspectOut2 = null;
    public int amountOut2 = 0;
    public final int maxOut = 16;

    public int processTime = 0;
    public final int maxProcessTime = 40; // Example process time
    public boolean working = false;

    public CentrifugeLogic(Runnable setChanged) {
        this.setChanged = setChanged;
    }

    public void tick() {
        boolean dirty = false;

        if (amountIn > 0 && aspectIn != null && !aspectIn.isPrimal()) {
            Aspect[] components = aspectIn.getComponents();
            if (components != null && components.length == 2) {
                // Check if we can fit the output
                boolean canFit1 = (aspectOut1 == null || aspectOut1 == components[0]) && amountOut1 < maxOut;
                boolean canFit2 = (aspectOut2 == null || aspectOut2 == components[1]) && amountOut2 < maxOut;

                if (canFit1 && canFit2) {
                    working = true;
                    processTime++;
                    if (processTime >= maxProcessTime) {
                        amountIn--;
                        if (amountIn <= 0) aspectIn = null;

                        aspectOut1 = components[0];
                        amountOut1++;
                        aspectOut2 = components[1];
                        amountOut2++;

                        processTime = 0;
                    }
                    dirty = true;
                } else {
                    if (working) {
                        working = false;
                        dirty = true;
                    }
                }
            } else {
                if (working) {
                    working = false;
                    dirty = true;
                }
            }
        } else {
            if (working) {
                working = false;
                processTime = 0;
                dirty = true;
            } else if (processTime > 0) {
                processTime = 0;
                dirty = true;
            }
        }

        if (dirty) {
            setChanged.run();
        }
    }

    public boolean canInputFrom(Direction face) {
        return face == Direction.DOWN;
    }

    public boolean canOutputTo(Direction face) {
        return face == Direction.UP;
    }

    public Aspect getSuctionType(Direction face) {
        if (face == Direction.DOWN) {
            return aspectIn; // accept whatever or specific if buffered
        }
        return null;
    }

    public int getSuctionAmount(Direction face) {
        if (face == Direction.DOWN) {
            return amountIn < maxIn ? 64 : 0; // standard suction
        }
        return 0;
    }

    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (!canInputFrom(face) || aspect.isPrimal()) return 0;
        if (aspectIn != null && aspectIn != aspect) return 0;

        int added = Math.min(amount, maxIn - amountIn);
        if (added > 0) {
            aspectIn = aspect;
            amountIn += added;
            setChanged.run();
        }
        return added;
    }

    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (!canOutputTo(face)) return 0;

        int taken = 0;
        if (aspectOut1 == aspect && amountOut1 > 0) {
            taken = Math.min(amount, amountOut1);
            amountOut1 -= taken;
            if (amountOut1 <= 0) aspectOut1 = null;
        } else if (aspectOut2 == aspect && amountOut2 > 0) {
            taken = Math.min(amount, amountOut2);
            amountOut2 -= taken;
            if (amountOut2 <= 0) aspectOut2 = null;
        }

        if (taken > 0) {
            setChanged.run();
        }
        return taken;
    }

    public Aspect getEssentiaType(Direction face) {
        if (face == Direction.UP) {
            if (aspectOut1 != null) return aspectOut1;
            if (aspectOut2 != null) return aspectOut2;
        }
        return null;
    }

    public int getEssentiaAmount(Direction face) {
        if (face == Direction.UP) {
            return amountOut1 + amountOut2;
        }
        return 0;
    }

    public void save(ValueOutput output) {
        if (aspectIn != null) {
            output.store("AspectIn", Codec.STRING, aspectIn.getTag());
            output.store("AmountIn", Codec.INT, amountIn);
        }
        if (aspectOut1 != null) {
            output.store("AspectOut1", Codec.STRING, aspectOut1.getTag());
            output.store("AmountOut1", Codec.INT, amountOut1);
        }
        if (aspectOut2 != null) {
            output.store("AspectOut2", Codec.STRING, aspectOut2.getTag());
            output.store("AmountOut2", Codec.INT, amountOut2);
        }
        output.store("ProcessTime", Codec.INT, processTime);
        output.store("Working", Codec.BOOL, working);
    }

    public void load(ValueInput input) {
        aspectIn = input.read("AspectIn", Codec.STRING).map(Aspect::getAspect).orElse(null);
        amountIn = input.read("AmountIn", Codec.INT).orElse(0);

        aspectOut1 = input.read("AspectOut1", Codec.STRING).map(Aspect::getAspect).orElse(null);
        amountOut1 = input.read("AmountOut1", Codec.INT).orElse(0);

        aspectOut2 = input.read("AspectOut2", Codec.STRING).map(Aspect::getAspect).orElse(null);
        amountOut2 = input.read("AmountOut2", Codec.INT).orElse(0);

        processTime = input.read("ProcessTime", Codec.INT).orElse(0);
        working = input.read("Working", Codec.BOOL).orElse(false);
    }
}
