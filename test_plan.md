1. **Block implementation**:
   - Create `BlockCentrifuge` in `thaumcraft.common.blocks.essentia` overriding `Block`. Implement `EntityBlock`. It will have a `FACING` property if directional (or default `UP` depending on how it connects), but for a centrifuge, it usually takes suction from the bottom, outputs to top (tube). Actually, in standard TC it connects on all sides. Let's make it a simple block.
   - Update `ThaumcraftBlocks` to register `centrifuge` as `BlockCentrifuge` instead of `registerSimpleBlock`.

2. **Block Entity implementation**:
   - Create `CentrifugeBlockEntity` in `thaumcraft.common.tiles.essentia`.
   - Extend `BlockEntity`. Implement `IEssentiaTransport`, `IAspectContainer` maybe. Wait, I should look up if a centrifuge is just `IEssentiaTransport` or `ITickable`.
   - Logic: Centrifuge separates compound essentia into component aspects (e.g. `Victus` -> `Terra` + `Aqua`).
   - Need to register `CENTRIFUGE` in `ThaumcraftBlockEntities`.

3. **Math/Logic decoupling**:
   - Create `CentrifugeLogic` in `thaumcraft.common.tiles.essentia` to handle the aspect breakdown.
   - Pass in `Runnable` to mark dirty.

4. **Tests**:
   - Create `CentrifugeLogicTest` testing `Victus -> Terra + Aqua`, `Air -> fails`, etc.

Wait, I should check how we use `CentrifugeLogic` independently of standard Minecraft `Level` objects so it's unit testable.
It should just maintain state:
- `aspectIn`: the aspect waiting to be separated.
- `amountIn`: how much is queued.
- `aspectOut1`, `amountOut1`: result 1 buffer
- `aspectOut2`, `amountOut2`: result 2 buffer
- A process method `tick()` that advances a progress timer, and when max is reached, decreases `amountIn` by 1 and increases `amountOut`s by 1.
- Provides `takeEssentia`, `addEssentia`, etc.

Let's write this down.
