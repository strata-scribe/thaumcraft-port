package thaumcraft.common.entities.monster.pech;

public class PechDialogueLogic {

    public enum Mood {
        ANGRY,
        NEUTRAL,
        FRIENDLY
    }

    /**
     * Evaluates how the Pech's mood transitions based on the barter value of an offered item.
     *
     * @param currentMood The current mood of the Pech
     * @param barterValue The evaluated value of the item being offered
     * @return The new mood of the Pech
     */
    public static Mood evaluateMoodTransition(Mood currentMood, int barterValue) {
        if (currentMood == Mood.ANGRY) {
            // It takes a very high value item to calm an angry Pech
            if (barterValue >= 10) {
                return Mood.NEUTRAL;
            }
        } else if (currentMood == Mood.NEUTRAL) {
            // A good value item makes a neutral Pech friendly
            if (barterValue >= 15) {
                return Mood.FRIENDLY;
            } else if (barterValue >= 5) {
                return Mood.NEUTRAL;
            }
        } else if (currentMood == Mood.FRIENDLY) {
            // A terrible item angers a friendly Pech
            if (barterValue == 0) {
                return Mood.ANGRY;
            } else if (barterValue < 5) {
                return Mood.NEUTRAL;
            }
        }

        // If they offer garbage and the pech is already angry or neutral, they stay or become angry
        if (barterValue == 0) {
            return Mood.ANGRY;
        }

        return currentMood;
    }

    /**
     * Evaluates the item offer and returns a dialogue response code based on the Pech's mood and the item's value.
     * Response Codes:
     * 0: Pech is uninterested/insulted (Value == 0)
     * 1: Pech is somewhat interested but demands more (Low value)
     * 2: Pech accepts the barter and is pleased (Good value)
     * 3: Pech is ecstatic and offers something great (High value, especially when Friendly)
     * -1: Pech is too angry to trade
     *
     * @param mood The current mood of the Pech
     * @param barterValue The evaluated value of the item being offered
     * @return Integer response code
     */
    public static int getDialogueResponse(Mood mood, int barterValue) {
        if (mood == Mood.ANGRY) {
            if (barterValue >= 10) {
                return 1; // "Fine, I'll take it, but I'm still mad."
            }
            return -1; // "Grrr!"
        }

        if (barterValue == 0) {
            return 0; // "Worthless junk!"
        }

        if (mood == Mood.FRIENDLY) {
            if (barterValue >= 15) {
                return 3; // Ecstatic
            } else if (barterValue >= 5) {
                return 2; // Pleased
            } else {
                return 1; // Demands more
            }
        }

        // NEUTRAL
        if (barterValue >= 15) {
            return 2; // Pleased
        } else if (barterValue >= 5) {
            return 1; // Interested but wants more
        } else {
            return 0; // Uninterested
        }
    }
}
