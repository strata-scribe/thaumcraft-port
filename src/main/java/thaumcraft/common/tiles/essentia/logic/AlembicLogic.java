package thaumcraft.common.tiles.essentia.logic;

import thaumcraft.api.aspects.Aspect;

public class AlembicLogic {
    public static final int MAX_AMOUNT = 32;

    private Aspect aspect = null;
    private Aspect aspectFilter = null;
    private int amount = 0;

    public Aspect getAspect() {
        return aspect;
    }

    public int getAmount() {
        return amount;
    }

    public Aspect getAspectFilter() {
        return aspectFilter;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(MAX_AMOUNT, amount));
        if (this.amount == 0) {
            this.aspect = null;
        }
    }

    public void setAspectFilter(Aspect aspectFilter) {
        this.aspectFilter = aspectFilter;
    }

    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || tag == aspectFilter;
    }

    public int addToContainer(Aspect tag, int am) {
        if (!doesContainerAccept(tag)) return am;
        if (amount > 0 && tag != aspect) return am;

        if (amount == 0 || aspect == null) {
            aspect = tag;
        }

        int canAdd = Math.min(am, MAX_AMOUNT - amount);
        amount += canAdd;

        if (amount == 0) {
            aspect = null;
        }

        return am - canAdd;
    }

    public boolean takeFromContainer(Aspect tag, int am) {
        if (amount == 0 || aspect == null) {
            aspect = null;
            amount = 0;
            return false;
        }
        if (aspect == tag && amount >= am) {
            amount -= am;
            if (amount <= 0) {
                aspect = null;
                amount = 0;
            }
            return true;
        }
        return false;
    }

    public boolean doesContainerContainAmount(Aspect tag, int am) {
        return tag == aspect && amount >= am;
    }

    public int containerContains(Aspect tag) {
        return tag == aspect ? amount : 0;
    }

    public void clearEssentia() {
        this.aspect = null;
        this.amount = 0;
    }
}
