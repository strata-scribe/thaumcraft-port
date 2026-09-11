1. **Create `fundamentals.json`**:
   - Path: `src/main/resources/assets/thaumcraft/research/fundamentals.json`.
   - Category: "FUNDAMENTALS"
   - Include nodes: "SALISMUNDUS", "ORES", "VISCRYSTALS", "PLANTS", "AURA".
   - Include `warp` in the stage definition for some entries (per prompt: "Validate JSON schema: stages, icons, parents, warp ratings."). I'll add `warp: 1` to one or more stages.
   - Include `parents` array to test parent node schema.
2. **Update `ResearchFundamentalsJsonTest`**:
   - Write a unit test using standard JSON parsing or directly loading via `ResearchEntry.CODEC.parse`.
   - The test will read `fundamentals.json` and ensure "SALISMUNDUS", "ORES", "VISCRYSTALS", "PLANTS", "AURA" are present and that their stages, icons, parents, and warp fields can be safely loaded.
3. **Pre-commit step**: Run `pre_commit_instructions` and follow them to ensure proper testing, verification, review, and reflection.
4. **Run tests**: `./gradlew test` must pass 100%.
