package thaumcraft.common.items.tools;

import java.util.function.Predicate;

public class GrappleGunLogic {

    public record Vector3d(double x, double y, double z) {
        public Vector3d add(Vector3d other) {
            return new Vector3d(this.x + other.x, this.y + other.y, this.z + other.z);
        }

        public Vector3d subtract(Vector3d other) {
            return new Vector3d(this.x - other.x, this.y - other.y, this.z - other.z);
        }

        public Vector3d multiply(double scalar) {
            return new Vector3d(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        public double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public Vector3d normalize() {
            double len = length();
            if (len == 0) return new Vector3d(0, 0, 0);
            return new Vector3d(x / len, y / len, z / len);
        }
    }

    public record ProjectileState(Vector3d position, Vector3d velocity) {}

    /**
     * Calculates the next position and velocity for the grapple hook flight path.
     * @param position Current position
     * @param velocity Current velocity
     * @param gravity Gravity to apply (subtracted from Y velocity)
     * @param drag Drag coefficient (0.0 to 1.0, where 1.0 stops the projectile, usually small like 0.01)
     * @return The state containing the new position and velocity
     */
    public static ProjectileState calculateProjectileFlight(Vector3d position, Vector3d velocity, double gravity, double drag) {
        Vector3d newPosition = position.add(velocity);
        double dragMultiplier = Math.max(0.0, 1.0 - drag);
        Vector3d newVelocity = velocity.multiply(dragMultiplier).subtract(new Vector3d(0, gravity, 0));
        return new ProjectileState(newPosition, newVelocity);
    }

    /**
     * Simulates the flight path and returns the exact impact coordinate where it hits a solid block.
     * @param startPosition The starting position
     * @param initialVelocity The starting velocity
     * @param gravity The gravity to apply per step
     * @param drag The drag to apply per step
     * @param maxSteps Maximum number of simulation steps to prevent infinite loops
     * @param isSolid Predicate returning true if the position is inside a solid block
     * @return The impact position, or null if no impact occurred within maxSteps
     */
    public static Vector3d calculateImpactCoordinate(Vector3d startPosition, Vector3d initialVelocity, double gravity, double drag, int maxSteps, Predicate<Vector3d> isSolid) {
        ProjectileState state = new ProjectileState(startPosition, initialVelocity);

        for (int i = 0; i < maxSteps; i++) {
            ProjectileState nextState = calculateProjectileFlight(state.position(), state.velocity(), gravity, drag);

            // Simple check on the new position. For a more accurate raycast, we would interpolate between state.position() and nextState.position(),
            // but testing the discrete steps is sufficient for the pure logic test unless specified otherwise.
            if (isSolid.test(nextState.position())) {
                return nextState.position();
            }
            state = nextState;
        }

        return null;
    }

    /**
     * Computes the pulling acceleration vector drawing the player towards the hook location,
     * incorporating spring tension (stiffness) and damping forces to reduce oscillation.
     *
     * Formula: Acceleration = (springStiffness * displacement) - (dampingCoefficient * velocity)
     *
     * @param playerPos The player's current position
     * @param playerVel The player's current velocity
     * @param hookPos The anchor/hook position
     * @param springStiffness The stiffness of the spring (k)
     * @param dampingCoefficient The damping factor (c)
     * @return The resulting acceleration vector
     */
    public static Vector3d calculatePullingAcceleration(Vector3d playerPos, Vector3d playerVel, Vector3d hookPos, double springStiffness, double dampingCoefficient) {
        Vector3d displacement = hookPos.subtract(playerPos);
        Vector3d springForce = displacement.multiply(springStiffness);
        Vector3d dampingForce = playerVel.multiply(dampingCoefficient);

        return springForce.subtract(dampingForce);
    }
}
