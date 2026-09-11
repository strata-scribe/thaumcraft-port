package thaumcraft.common.tiles.crafting;

public class InfusionStateMachineLogic {
    public enum State {
        IDLE,
        SCANNING,
        CRAFTING_ESSENTIA,
        CRAFTING_ITEMS,
        SUCCESS,
        INTERRUPTED
    }

    private State currentState = State.IDLE;
    private int progressTicks = 0;
    private int cycleTime = 20;
    private int countDelay = 10;
    private int itemConsumptionDelay = 0;

    public State getCurrentState() {
        return currentState;
    }

    public int getProgressTicks() {
        return progressTicks;
    }

    public int getEssentiaDrainInterval() {
        return countDelay;
    }

    public int getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(int cycleTime) {
        this.cycleTime = cycleTime;
        this.countDelay = Math.max(1, cycleTime / 2);
    }

    public void startScanning() {
        if (currentState == State.IDLE || currentState == State.SUCCESS || currentState == State.INTERRUPTED) {
            currentState = State.SCANNING;
            progressTicks = 0;
        }
    }

    public void startCraftingEssentia() {
        if (currentState == State.SCANNING) {
            currentState = State.CRAFTING_ESSENTIA;
            progressTicks = 0;
        }
    }

    public void startCraftingItems() {
        if (currentState == State.CRAFTING_ESSENTIA) {
            currentState = State.CRAFTING_ITEMS;
            progressTicks = 0;
            itemConsumptionDelay = 0;
        }
    }

    public void complete() {
        if (currentState == State.CRAFTING_ITEMS) {
            currentState = State.SUCCESS;
            progressTicks = 0;
        }
    }

    public void interrupt() {
        if (currentState != State.IDLE && currentState != State.SUCCESS) {
            currentState = State.INTERRUPTED;
            progressTicks = 0;
        }
    }

    public void reset() {
        currentState = State.IDLE;
        progressTicks = 0;
        itemConsumptionDelay = 0;
    }

    public void tick() {
        if (currentState == State.SCANNING || currentState == State.CRAFTING_ESSENTIA || currentState == State.CRAFTING_ITEMS) {
            progressTicks++;
        }
    }

    public boolean isReadyForCycle() {
        return (currentState == State.CRAFTING_ESSENTIA || currentState == State.CRAFTING_ITEMS)
                && progressTicks > 0
                && progressTicks % countDelay == 0;
    }

    public boolean shouldConsumeItem() {
        if (currentState != State.CRAFTING_ITEMS) {
            return false;
        }

        if (itemConsumptionDelay == 0) {
            itemConsumptionDelay = 5;
            return false;
        } else {
            itemConsumptionDelay--;
            if (itemConsumptionDelay <= 1) {
                itemConsumptionDelay = 0; // Reset for next item
                return true;
            }
            return false;
        }
    }

    public int getItemConsumptionDelay() {
        return itemConsumptionDelay;
    }
}
