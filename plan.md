1. **Create `src/test/java/thaumcraft/common/lib/research/ResearchAuromancyJsonValidationTest.java`**:
   - Write a JUnit 5 test class to load `auromancy.json` manually with zero Minecraft/Forge dependencies (no registry lookups, just standard Java JSON parsers like Gson).
2. **Verify JSON parses without syntax errors**:
   - Use Gson `JsonParser.parseReader()` on the InputStream of `/assets/thaumcraft/research/auromancy.json`.
3. **Verify focus nodes and complexity requirements**:
   - Iterate over the entries and stages.
   - For each entry, check if `focus_nodes` exists; if it does, assert it's a valid number.
   - For each stage, check if `complexity` exists; if it does, assert it's a valid number.
   - The user mentions "focus nodes and complexity requirements". Maybe I should also check `focus_nodes` and `complexity` fields on entries? I will check both `entries` and their `stages` and any sub-objects for `focus_nodes` and `complexity` and verify they are numbers. Or I could just do a recursive search in the JSON for those keys and assert they are Numbers.
4. **Zero Minecraft registry lookups**:
   - Do not instantiate `ResearchEntry` via the `CODEC` that does `BuiltInRegistries.ITEM.getOptional()` because the prompt emphasizes this. Only use standard Gson validation.
5. **Pre-commit and Test**:
   - Ensure `./gradlew test --tests *ResearchAuromancyJsonValidationTest*` passes.
   - Perform pre-commit checks.
