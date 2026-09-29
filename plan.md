1. **Implement `ItemCloudRing` in `src/main/java/thaumcraft/common/items/curios/ItemCloudRing.java`:**
   - Extend `net.minecraft.world.item.Item`.
   - Implement the slow falling effect in the `inventoryTick` method. This handles the item ticking when placed in Curios slots.
   - Extract logic to `CloudRingLogic` pure Java class for tests.
2. **Implement `CloudRingLogic` in `src/main/java/thaumcraft/common/items/curios/CloudRingLogic.java`:**
   - Add `shouldApplySlowFall(boolean isSneaking, boolean isOnGround, boolean hasCloudRing)` returning true if sneaking, not on ground, and has ring.
3. **Write `ItemCloudRingTest` in `src/test/java/thaumcraft/common/items/curios/ItemCloudRingTest.java`:**
   - Implement `src/test/java/thaumcraft/common/items/curios/ItemCloudRingTest.java`, adding test methods to verify `shouldApplySlowFall` under various conditions (e.g., sneaking vs. not sneaking, on ground vs. in air).
4. **Register `ItemCloudRing`:**
   - Replace the `p -> p` stub in `ThaumcraftItems` with `thaumcraft.common.items.curios.ItemCloudRing::new`.
5. **Verify changes**: Use `read_file` to verify the edits made to `src/main/java/thaumcraft/api/items/ThaumcraftItems.java` and confirm the newly created files are correct.
6. **Run tests**: Run the tests to ensure no regressions are introduced by executing the command `./gradlew compileJava test`.
7. **Complete pre-commit steps**
   - Address PR review: created `src/main/resources/data/curios/tags/item/ring.json`, changed `inventoryTick` correctly.
   - Complete pre commit steps to make sure proper testing, verifications, reviews and reflections are done.
8. **Submit**.
