package thaumcraft.common.entities.monster.wisp;

import java.util.List;

public class WispCombatLogic {

    /**
     * Calculates the wandering flight vector for a Wisp.
     * It dodges obstacles, and avoids player line of sight unless the Wisp is attacked.
     *
     * @param wispPos Current position of the Wisp [x, y, z].
     * @param playerPos Position of the player [x, y, z].
     * @param playerLook Look vector of the player [x, y, z].
     * @param isAttacked Whether the Wisp is currently being attacked (aggroed).
     * @param obstacles List of positions [x, y, z] of obstacles nearby.
     * @return The calculated flight direction vector [x, y, z], normalized.
     */
    public static double[] calculateWanderFlightVector(double[] wispPos, double[] playerPos, double[] playerLook, boolean isAttacked, List<double[]> obstacles) {
        double[] flightVector = new double[3];

        // 1. Avoid obstacles
        double obstacleRepulsionX = 0;
        double obstacleRepulsionY = 0;
        double obstacleRepulsionZ = 0;
        double repulsionWeight = 2.0;

        for (double[] obs : obstacles) {
            double dx = wispPos[0] - obs[0];
            double dy = wispPos[1] - obs[1];
            double dz = wispPos[2] - obs[2];
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > 0 && distSq < 25.0) { // Only repulse if within 5 blocks
                double dist = Math.sqrt(distSq);
                double force = (5.0 - dist) / 5.0; // Stronger as it gets closer
                obstacleRepulsionX += (dx / dist) * force * repulsionWeight;
                obstacleRepulsionY += (dy / dist) * force * repulsionWeight;
                obstacleRepulsionZ += (dz / dist) * force * repulsionWeight;
            }
        }

        // 2. React to player
        double playerReactX = 0;
        double playerReactY = 0;
        double playerReactZ = 0;

        if (playerPos != null && playerLook != null) {
            double toWispX = wispPos[0] - playerPos[0];
            double toWispY = wispPos[1] - playerPos[1];
            double toWispZ = wispPos[2] - playerPos[2];
            double toWispDist = Math.sqrt(toWispX * toWispX + toWispY * toWispY + toWispZ * toWispZ);

            if (toWispDist > 0) {
                if (isAttacked) {
                    // Fly towards player if attacked, but maintain a small distance
                    double optimalDistance = 3.0;
                    if (toWispDist > optimalDistance) {
                        playerReactX = -toWispX / toWispDist;
                        playerReactY = -toWispY / toWispDist;
                        playerReactZ = -toWispZ / toWispDist;
                    } else if (toWispDist < optimalDistance) {
                        playerReactX = toWispX / toWispDist;
                        playerReactY = toWispY / toWispDist;
                        playerReactZ = toWispZ / toWispDist;
                    }
                } else {
                    // Avoid line of sight if not attacked
                    // Normalize look vector
                    double lookLen = Math.sqrt(playerLook[0] * playerLook[0] + playerLook[1] * playerLook[1] + playerLook[2] * playerLook[2]);
                    double normLookX = playerLook[0] / lookLen;
                    double normLookY = playerLook[1] / lookLen;
                    double normLookZ = playerLook[2] / lookLen;

                    // Normalize toWisp
                    double normToWispX = toWispX / toWispDist;
                    double normToWispY = toWispY / toWispDist;
                    double normToWispZ = toWispZ / toWispDist;

                    double dotProduct = normLookX * normToWispX + normLookY * normToWispY + normLookZ * normToWispZ;

                    // If player is looking roughly towards the wisp (dot product > 0.5 ~ 60 degrees)
                    if (dotProduct > 0.5) {
                        // Move perpendicular to line of sight
                        // Cross product of look vector and up vector (0, 1, 0)
                        double perpX = normLookZ;
                        double perpY = 0.5; // slight upward drift
                        double perpZ = -normLookX;

                        double perpLen = Math.sqrt(perpX * perpX + perpY * perpY + perpZ * perpZ);
                        if (perpLen > 0) {
                             // we check if moving left or right takes us further from center line
                             double moveLeftDot = perpX * normToWispX + perpY * normToWispY + perpZ * normToWispZ;
                             if (moveLeftDot < 0) {
                                 perpX = -perpX;
                                 perpZ = -perpZ;
                             }
                             playerReactX = perpX / perpLen;
                             playerReactY = perpY / perpLen;
                             playerReactZ = perpZ / perpLen;
                        }
                    }
                }
            }
        }

        flightVector[0] = obstacleRepulsionX + playerReactX;
        flightVector[1] = obstacleRepulsionY + playerReactY;
        flightVector[2] = obstacleRepulsionZ + playerReactZ;

        // If no significant movement, add a slight random wander
        double lenSq = flightVector[0] * flightVector[0] + flightVector[1] * flightVector[1] + flightVector[2] * flightVector[2];
        if (lenSq < 0.01) {
             flightVector[0] = (Math.random() - 0.5) * 0.5;
             flightVector[1] = (Math.random() - 0.5) * 0.5;
             flightVector[2] = (Math.random() - 0.5) * 0.5;
        }

        // Normalize final vector
        double finalLen = Math.sqrt(flightVector[0] * flightVector[0] + flightVector[1] * flightVector[1] + flightVector[2] * flightVector[2]);
        if (finalLen > 0) {
            flightVector[0] /= finalLen;
            flightVector[1] /= finalLen;
            flightVector[2] /= finalLen;
        }

        return flightVector;
    }

    /**
     * Calculates the damage of a Wisp's spark counter-attack based on its aspect.
     *
     * @param baseDamage The base damage of the spark.
     * @param aspectTag The aspect tag of the Wisp (e.g. "ignis", "aer", etc). Null or empty if none.
     * @return The modified elemental damage.
     */
    public static float calculateSparkDamage(float baseDamage, String aspectTag) {
        if (aspectTag == null || aspectTag.isEmpty()) {
            return baseDamage;
        }

        switch (aspectTag.toLowerCase()) {
            case "ignis": // Fire - deals extra damage
                return baseDamage * 1.5f;
            case "perditio": // Entropy - massive damage
                return baseDamage * 2.0f;
            case "aer": // Air - less direct damage but might have other effects in MC
                return baseDamage * 0.8f;
            case "terra": // Earth - solid damage
                return baseDamage * 1.2f;
            case "aqua": // Water - standard damage
                return baseDamage * 1.0f;
            case "ordo": // Order - precise, slightly elevated damage
                return baseDamage * 1.1f;
            default:
                // Compound aspects or others provide slight bonus
                return baseDamage * 1.05f;
        }
    }
}
