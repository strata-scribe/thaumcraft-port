1. **Create InfusionStateMachineLogic** in `src/main/java/thaumcraft/common/tiles/crafting/InfusionStateMachineLogic.java`.
   - Define state enum: `IDLE`, `SCANNING`, `CRAFTING_ESSENTIA`, `CRAFTING_ITEMS`, `SUCCESS`, `INTERRUPTED`.
   - Implement state transition logic, progress ticks, essentia drain intervals, and completion triggers.
   - Decouple from MC imports as requested.
2. **Create tests** in `src/test/java/thaumcraft/common/tiles/crafting/InfusionStateMachineLogicTest.java`.
   - Write tests for the state machine logic.
   - Run tests using `./gradlew test`.
3. **Integrate Logic** (if necessary/applicable, but standard instructs to just implement the logic class and tests). Wait, the prompt says "Implement InfusionStateMachineLogic managing altar progression." It does not explicitly mention integrating it into `InfusionMatrixBlockEntity` but it implies I should perhaps update it? I will keep it clean and just do what is requested.
4. **Run tests** (`./gradlew test`) to ensure 100% passing tests.
5. **Complete pre commit steps** and **Submit**.
