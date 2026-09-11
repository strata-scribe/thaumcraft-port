package thaumcraft.common.lib.alchemy;

public class MetalPurificationLogic {

    private static final int DEFAULT_REQUIRED_METALLUM = 5;
    private static final int DEFAULT_REQUIRED_ORDO = 5;

    /**
     * Checks if a raw ore or cluster can be purified into a Native Cluster
     * in a crucible using Metallum and Ordo aspects.
     *
     * @param isRawOreOrCluster true if the input item is a raw ore or cluster
     * @param metallumAmount amount of Metallum aspect present
     * @param ordoAmount amount of Ordo aspect present
     * @return true if the item can be purified, false otherwise
     */
    public static boolean canPurify(boolean isRawOreOrCluster, int metallumAmount, int ordoAmount) {
        return canPurify(isRawOreOrCluster, metallumAmount, DEFAULT_REQUIRED_METALLUM, ordoAmount, DEFAULT_REQUIRED_ORDO);
    }

    /**
     * Checks if a raw ore or cluster can be purified into a Native Cluster
     * in a crucible using custom amounts of Metallum and Ordo aspects.
     *
     * @param isRawOreOrCluster true if the input item is a raw ore or cluster
     * @param metallumAmount amount of Metallum aspect present
     * @param requiredMetallum amount of Metallum required
     * @param ordoAmount amount of Ordo aspect present
     * @param requiredOrdo amount of Ordo required
     * @return true if the item can be purified, false otherwise
     */
    public static boolean canPurify(boolean isRawOreOrCluster, int metallumAmount, int requiredMetallum, int ordoAmount, int requiredOrdo) {
        if (!isRawOreOrCluster) {
            return false;
        }
        return metallumAmount >= requiredMetallum && ordoAmount >= requiredOrdo;
    }

    /**
     * Determines the smelting yield when an item is smelted in a furnace.
     * Native Clusters yield 2x the normal ingot amount.
     *
     * @param isNativeCluster true if the item being smelted is a Native Cluster
     * @param baseYield the base yield of the smelting recipe (usually 1)
     * @return the calculated yield
     */
    public static int getSmeltingYield(boolean isNativeCluster, int baseYield) {
        if (isNativeCluster) {
            return baseYield * 2;
        }
        return baseYield;
    }
}
