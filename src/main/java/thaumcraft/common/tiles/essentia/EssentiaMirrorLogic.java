package thaumcraft.common.tiles.essentia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EssentiaMirrorLogic {

    private final Map<String, MirrorData> mirrors = new HashMap<>();

    public static class TransportEvent {
        public final String aspect;
        public final int amount;
        public int delay;
        public final Runnable callback;

        public TransportEvent(String aspect, int amount, int delay, Runnable callback) {
            this.aspect = aspect;
            this.amount = amount;
            this.delay = delay;
            this.callback = callback;
        }
    }

    public static class MirrorData {
        public final String id;
        public String pairedId;
        public String suctionAspect;
        public int suctionAmount;
        public float pendingVisCost;
        public final List<TransportEvent> transportQueue = new ArrayList<>();

        public MirrorData(String id) {
            this.id = id;
        }
    }

    public void registerMirror(String id) {
        mirrors.putIfAbsent(id, new MirrorData(id));
    }

    public void unregisterMirror(String id) {
        MirrorData mirror = mirrors.remove(id);
        if (mirror != null && mirror.pairedId != null) {
            MirrorData paired = mirrors.get(mirror.pairedId);
            if (paired != null && id.equals(paired.pairedId)) {
                paired.pairedId = null;
            }
        }
    }

    public void pairMirrors(String id1, String id2) {
        MirrorData m1 = mirrors.get(id1);
        MirrorData m2 = mirrors.get(id2);
        if (m1 != null && m2 != null) {
            m1.pairedId = id2;
            m2.pairedId = id1;
        }
    }

    public void unpairMirror(String id) {
        MirrorData m = mirrors.get(id);
        if (m != null && m.pairedId != null) {
            MirrorData paired = mirrors.get(m.pairedId);
            if (paired != null && id.equals(paired.pairedId)) {
                paired.pairedId = null;
            }
            m.pairedId = null;
        }
    }

    public void setSuction(String id, String aspect, int amount) {
        MirrorData m = mirrors.get(id);
        if (m != null) {
            m.suctionAspect = aspect;
            m.suctionAmount = amount;
        }
    }

    public String getBroadcastSuctionAspect(String id) {
        MirrorData m = mirrors.get(id);
        if (m != null && m.pairedId != null) {
            MirrorData paired = mirrors.get(m.pairedId);
            if (paired != null) {
                return paired.suctionAspect;
            }
        }
        return null;
    }

    public int getBroadcastSuctionAmount(String id) {
        MirrorData m = mirrors.get(id);
        if (m != null && m.pairedId != null) {
            MirrorData paired = mirrors.get(m.pairedId);
            if (paired != null) {
                return paired.suctionAmount;
            }
        }
        return 0;
    }

    public void sendEssentia(String sourceId, String aspect, int amount, Runnable onComplete) {
        MirrorData m = mirrors.get(sourceId);
        if (m != null && m.pairedId != null) {
            MirrorData paired = mirrors.get(m.pairedId);
            if (paired != null) {
                m.pendingVisCost += 0.1f * amount;
                paired.transportQueue.add(new TransportEvent(aspect, amount, 5, onComplete));
            }
        }
    }

    public float getPendingVisCost(String id) {
        MirrorData m = mirrors.get(id);
        return m != null ? m.pendingVisCost : 0;
    }

    public float consumeVisCost(String id) {
        MirrorData m = mirrors.get(id);
        if (m != null) {
            float cost = m.pendingVisCost;
            m.pendingVisCost = 0;
            return cost;
        }
        return 0;
    }

    public void tick(String id) {
        MirrorData m = mirrors.get(id);
        if (m != null) {
            Iterator<TransportEvent> it = m.transportQueue.iterator();
            while (it.hasNext()) {
                TransportEvent event = it.next();
                if (event.delay > 0) {
                    event.delay--;
                }
                if (event.delay <= 0) {
                    if (event.callback != null) {
                        event.callback.run();
                    }
                    it.remove();
                }
            }
        }
    }

    public void tickAll() {
        for (String id : mirrors.keySet()) {
            tick(id);
        }
    }
}
