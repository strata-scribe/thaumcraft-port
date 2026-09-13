1. **Create `TaintedLeavesDecayLogic` class**
   - Create the file `src/main/java/thaumcraft/common/blocks/logic/TaintedLeavesDecayLogic.java`.
   - Implement the `calculateDesiccation` method to simulate leaf desiccation based on current state and proximity to logs.
   - Implement the `determineDrops` method to calculate the chance of dropping taint fibers versus pure saplings (incorporating fortune and random rolls).
   - Ensure 0 `net.minecraft` or `net.neoforged` imports.

2. **Create `TaintedLeavesDecayLogicTest` class**
   - Create the file `src/test/java/thaumcraft/common/blocks/logic/TaintedLeavesDecayLogicTest.java`.
   - Write tests for `calculateDesiccation` (checking bounds and distance logic).
   - Write tests for `determineDrops` (verifying proper drops based on probabilities and fortune levels).
   - Use JUnit 5 (`org.junit.jupiter.api.Test`).

3. **Complete pre commit steps**
   - Complete pre commit steps to make sure proper testing, verifications, reviews and reflections are done.
   - Run `./gradlew test` to verify everything compiles and tests pass 100%.

4. **Submit changes**
   - Commit and submit the code once tests pass.
