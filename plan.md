1. **Create `EldritchObeliskStructureLogic.java`**:
   - Use `write_file` to write `src/main/java/thaumcraft/common/world/logic/EldritchObeliskStructureLogic.java`.
   - Implement pure Java methods for the Eldritch Obelisk logic to replace the old ones in `WorldgenTreeLogic` and meet the new requirements ("12-block tall obsidian pillar with Eldritch Capstone at peak, surrounded by glyph-carved stones" and "Calculates structure bounding box and piece offsets").
   - Methods will include: `isPlatformBlock`, `isPedestal`, `isSpireBlock`, `getSpireHeight` (returns 12), `isCapstone`, `isGlyphCarvedStone` (using the logic `(Math.abs(dx) == 2 && Math.abs(dz) <= 2) || (Math.abs(dz) == 2 && Math.abs(dx) <= 2)`), `calculateBoundingBox`, and `getPieceOffsets`.

2. **Create `EldritchObeliskStructureLogicTest.java`**:
   - Use `write_file` to write `src/test/java/thaumcraft/common/world/logic/EldritchObeliskStructureLogicTest.java`.
   - Write comprehensive unit tests for `EldritchObeliskStructureLogic`.

3. **Verify Created Files**:
   - Use `read_file` to verify the contents of `EldritchObeliskStructureLogic.java` and `EldritchObeliskStructureLogicTest.java` to ensure they were written correctly.

4. **Modify Existing Code**:
   - Use `replace_with_git_merge_diff` to modify `src/main/java/thaumcraft/common/world/features/WorldgenTreeLogic.java` to remove the old Eldritch Obelisk procedural geometry methods (`isObeliskPlatformBlock`, `isObeliskPedestal`, `isObeliskSpireBlock`, `calculateObeliskSpireHeight`, `isObeliskCapstone`).
   - Use `replace_with_git_merge_diff` to modify `src/main/java/thaumcraft/common/world/features/EldritchObeliskFeature.java` to use the new `EldritchObeliskStructureLogic`, setting the spire height to 12 and the pillar block to obsidian, and using the new `isGlyphCarvedStone` check.
   - Use `replace_with_git_merge_diff` to modify `src/test/java/thaumcraft/common/world/WorldgenTreeLogicTest.java` to remove tests for the old Eldritch Obelisk methods.

5. **Verify Modified Files**:
   - Use `read_file` to check `src/main/java/thaumcraft/common/world/features/WorldgenTreeLogic.java`, `src/main/java/thaumcraft/common/world/features/EldritchObeliskFeature.java`, and `src/test/java/thaumcraft/common/world/WorldgenTreeLogicTest.java`.

6. **Execute Tests**:
   - Use `run_in_bash_session` to run `./gradlew test` to ensure 100% test coverage and pass rate for the new test class.

7. **Pre-commit Steps**:
   - Complete pre-commit steps to ensure proper testing, verification, review, and reflection are done by calling the `pre_commit_instructions` tool.

8. **Submit Changes**:
   - Use `submit` to commit and push to a new branch.
