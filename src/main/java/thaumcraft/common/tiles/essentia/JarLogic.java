package thaumcraft.common.tiles.essentia;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class JarLogic {
    public static final int CAPACITY = 250;
    public static final int PHIAL_AMOUNT = 10;

    private Aspect aspect = null;
    private int amount = 0;
    private Aspect aspectFilter = null;
    private boolean isVoid;

    public JarLogic(boolean isVoid) {
        this.isVoid = isVoid;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public int getAmount() {
        return amount;
    }

    public Aspect getAspectFilter() {
        return aspectFilter;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(CAPACITY, amount));
    }

    public void setAspectFilter(Aspect aspectFilter) {
        this.aspectFilter = aspectFilter;
    }

    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (aspect != null && amount > 0) {
            list.add(aspect, amount);
        }
        return list;
    }

    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            aspect = aspects.getAspectsSortedByAmount()[0];
            amount = aspects.getAmount(aspect);
        } else {
            aspect = null;
            amount = 0;
        }
    }

    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || tag == aspectFilter;
    }

    public int addToContainer(Aspect tag, int am) {
        if (am == 0) return 0;
        if (aspect != null && tag != aspect) return am;
        if (!doesContainerAccept(tag)) return am;

        aspect = tag;
        int canAdd = Math.min(am, CAPACITY - amount);
        amount += canAdd;

        if (isVoid) {
            return 0; // Infinite sink for accepted aspect
        }

        return am - canAdd;
    }

    public boolean takeFromContainer(Aspect tag, int am) {
        if (tag != aspect || amount < am) return false;
        amount -= am;
        if (amount <= 0) {
            amount = 0;
            aspect = null;
        }
        return true;
    }

    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return tag == aspect && amount >= amt;
    }

    public int containerContains(Aspect tag) {
        return tag == aspect ? amount : 0;
    }

    // Interaction results
    public enum InteractionResult {
        SUCCESS, PASS
    }

    public InteractionResult tryFillFromPhial(Aspect phialAspect, int phialAmount) {
        if (phialAspect != null && phialAmount > 0) {
            if (doesContainerAccept(phialAspect)
                    && (aspect == null || aspect == phialAspect)) {

                int canAdd = Math.min(phialAmount, CAPACITY - amount);
                // Can we add exactly the phial amount, or is it a void jar that will consume it all?
                if (canAdd == phialAmount || isVoid) {
                    addToContainer(phialAspect, phialAmount);
                    return InteractionResult.SUCCESS;
                }
                // If it can't accept the full amount, and it's not a void jar, we can't pour the phial completely
                // Actually, phial interactions are all-or-nothing in Thaumcraft 6.
                // Wait, if it can't take all 10, does it just not pour?
                // BlockJar.java previously said: jar.getAmount() + amt <= JarBlockEntity.CAPACITY
                // Let's keep that behavior:
                if (amount + phialAmount <= CAPACITY) {
                    addToContainer(phialAspect, phialAmount);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult tryDrainToPhial() {
        if (aspect != null && amount >= PHIAL_AMOUNT) {
            takeFromContainer(aspect, PHIAL_AMOUNT);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public InteractionResult applyLabel(Aspect labelAspect) {
        if (aspectFilter == null && labelAspect != null) {
            if (amount == 0 || aspect == labelAspect) {
                if (amount == 0) {
                    aspect = labelAspect;
                }
                aspectFilter = labelAspect;
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult removeLabel() {
        if (aspectFilter != null) {
            aspectFilter = null;
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
