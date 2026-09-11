1. **Create `JarLogic.java`**:
   - Extract jar logic from `JarBlockEntity` and `BlockJar` to a decoupled `JarLogic` class.
   - Decouple it to not have Minecraft imports (e.g. `ItemStack`, `Player`, `Level`, `BlockPos` shouldn't dictate core transaction logic).
   - Variables: `capacity`, `amount`, `aspect`, `aspectFilter`, `isVoid`.
   - Methods for:
     - `addToContainer(Aspect tag, int am)`: return leftover. Void jar will return 0 (infinite sink) when adding the currently locked/stored aspect.
     - `takeFromContainer(Aspect tag, int am)`: return success boolean.
     - `doesContainerAccept(Aspect tag)`: boolean
     - Phial interactions logic: fill from empty phial, empty to filled phial.
     - Labeling logic.
2. **Update `JarBlockEntity.java`**:
   - Remove internal fields `amount`, `aspect`, `aspectFilter`.
   - Replaced by a `JarLogic logic` instance.
   - Delegate `IAspectContainer` methods to `JarLogic`.
   - Subclass or set a flag for void jars? There is only one block entity `JarBlockEntity` but two blocks `jarNormal` and `jarVoid`. Wait, let's see how `BlockJar` distinguishes them. Actually `JarBlockEntity` just needs a boolean `isVoid` that is determined upon creation. Wait, the block entity type is one, maybe we pass `isVoid` in constructor or the Block Entity looks at block state / block.
   - We need to correctly handle void jar logic. The user request: "Warded Jar capacity (250 essentia), Void Jar infinite sink for locked aspect."
3. **Update `BlockJar.java`**:
   - Refactor `useItemOn` to use `JarLogic`'s transaction result, keeping UI logic (sound, dropping item).
4. **Create `JarLogicTest.java`**:
   - Add unit tests as requested.
