1. **Create `PechDialogueLogic.java`**:
   - Location: `src/main/java/thaumcraft/common/entities/monster/pech/PechDialogueLogic.java`
   - Implement `Mood` enum (ANGRY, NEUTRAL, FRIENDLY).
   - Method `evaluateMoodTransition(Mood currentMood, int barterValue)`: returns new Mood.
     - e.g., if ANGRY, barterValue > 10 -> NEUTRAL
     - if NEUTRAL, barterValue > 15 -> FRIENDLY
   - Method `getDialoguePrompt(Mood mood, int barterValue)`: returns dialogue string/code.
   - Decouple all logic from Minecraft imports.
2. **Create `PechDialogueLogicTest.java`**:
   - Location: `src/test/java/thaumcraft/common/entities/monster/pech/PechDialogueLogicTest.java`
   - Test all transitions and dialogue outputs.
3. **Pre-commit Instructions**:
   - Run `pre_commit_instructions` tool to verify.
