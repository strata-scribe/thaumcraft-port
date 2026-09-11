package thaumcraft.common.lib;

import java.util.Objects;

public class MagicMirrorLogic {

    public static class MirrorPosition {
        public final int x;
        public final int y;
        public final int z;
        public final String dimensionId;

        public MirrorPosition(int x, int y, int z, String dimensionId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimensionId = dimensionId != null ? dimensionId : "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MirrorPosition that = (MirrorPosition) o;
            return x == that.x && y == that.y && z == that.z && dimensionId.equals(that.dimensionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, dimensionId);
        }
    }

    /**
     * Validates if a mirror at the source location can be linked to a mirror at the target location.
     */
    public static boolean canLink(MirrorPosition source, MirrorPosition target) {
        if (source == null || target == null) return false;
        return !source.equals(target);
    }

    public static class DropCoordinates {
        public final double x;
        public final double y;
        public final double z;

        public DropCoordinates(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DropCoordinates that = (DropCoordinates) o;
            return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    /**
     * Calculates the exact drop coordinates for an item exiting a mirror.
     * @param targetPos The position of the target mirror.
     * @param normalX The x component of the mirror's facing normal (-1, 0, 1).
     * @param normalY The y component of the mirror's facing normal (-1, 0, 1).
     * @param normalZ The z component of the mirror's facing normal (-1, 0, 1).
     * @return The coordinates where the item should appear.
     */
    public static DropCoordinates calculateDropCoordinates(MirrorPosition targetPos, int normalX, int normalY, int normalZ) {
        // Output from the center of the block, pushed out slightly past the face
        double dropX = targetPos.x + 0.5 + normalX * 0.7;
        double dropY = targetPos.y + 0.5 + normalY * 0.7;
        double dropZ = targetPos.z + 0.5 + normalZ * 0.7;
        return new DropCoordinates(dropX, dropY, dropZ);
    }

    public interface MirrorStateProvider {
        boolean isDimensionLoaded(String dimensionId);
        boolean isMirrorAt(int x, int y, int z, String dimensionId);
    }

    /**
     * Checks if an item can be successfully sent to the target mirror.
     */
    public static boolean canSendItem(MirrorPosition link, MirrorStateProvider provider) {
        if (link == null || provider == null) return false;
        if (!provider.isDimensionLoaded(link.dimensionId)) return false;
        return provider.isMirrorAt(link.x, link.y, link.z, link.dimensionId);
    }
}
