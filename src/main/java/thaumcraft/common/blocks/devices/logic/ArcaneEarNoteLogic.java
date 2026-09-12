package thaumcraft.common.blocks.devices.logic;

public class ArcaneEarNoteLogic {

    /**
     * Checks if the ambient note block frequency matches the tuned parameters of the Arcane Ear.
     * @param ambientNote The note produced by the ambient event.
     * @param ambientInstrument The instrument produced by the ambient event (as a String).
     * @param tunedNote The note the Arcane Ear is tuned to.
     * @param tunedInstrument The instrument the Arcane Ear is tuned to (as a String).
     * @return True if they match, false otherwise.
     */
    public static boolean matches(int ambientNote, String ambientInstrument, int tunedNote, String tunedInstrument) {
        if (ambientInstrument == null || tunedInstrument == null) {
            return false;
        }
        return ambientNote == tunedNote && ambientInstrument.equals(tunedInstrument);
    }

    /**
     * Calculates the redstone pulse tick length output by the Arcane Ear when triggered.
     * @return The number of ticks (20 ticks = 1 second).
     */
    public static int getPulseLengthTicks() {
        return 20;
    }
}
