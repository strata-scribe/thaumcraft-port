package thaumcraft.common.golems.seals;

public class SealGatherLogic {

    public static class BoundingBox {
        public double minX, minY, minZ;
        public double maxX, maxY, maxZ;

        public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public boolean intersects(double eMinX, double eMinY, double eMinZ, double eMaxX, double eMaxY, double eMaxZ) {
            return this.minX < eMaxX && this.maxX > eMinX &&
                   this.minY < eMaxY && this.maxY > eMinY &&
                   this.minZ < eMaxZ && this.maxZ > eMinZ;
        }
    }

    public interface ItemAdapter {
        Object getEntityReference();
        double getMinX(); double getMinY(); double getMinZ();
        double getMaxX(); double getMaxY(); double getMaxZ();
        boolean isAlive();
        boolean hasPickUpDelay();
        int getStackSize();
        boolean matchesFilter();
    }

    public static class GatherTask {
        public Object entityReference;
        public byte priority;
        public short lifespan;

        public GatherTask(Object entityReference, byte priority, short lifespan) {
            this.entityReference = entityReference;
            this.priority = priority;
            this.lifespan = lifespan;
        }
    }

    public static GatherTask evaluateItemAndCreateTask(BoundingBox area, ItemAdapter item, byte sealPriority) {
        if (item != null && item.isAlive() && !item.hasPickUpDelay() && item.getStackSize() > 0) {
            if (area.intersects(item.getMinX(), item.getMinY(), item.getMinZ(), item.getMaxX(), item.getMaxY(), item.getMaxZ())) {
                if (item.matchesFilter()) {
                    return new GatherTask(item.getEntityReference(), sealPriority, (short) 300);
                }
            }
        }
        return null;
    }
}
