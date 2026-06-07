package game.enums;

/**
 * An enum representing the various environmental states or traits related to
 * spatial distortion within the game world.
 *
 * These capabilities are primarily applied to Ground objects to allow
 * systems and actors to identify specific tile properties, such as whether a tile
 * is dangerous, protective, or able to be restored.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public enum DistortionCapability {
    /**
     * Identifies grounds that have been affected by corruption.
     * Tiles with this capability are typically targets for stabilization actions
     * or may trigger specific status effects (like SanctuaryStatus) for workers.
     */
    CORRUPTED,

    /**
     * Identifies zones that provide safety or sanctuary to actors.
     * This is used to define the boundaries of protective areas.
     */
    SANCTUARY,

    /**
     * Identifies grounds that currently host a timed or active hazard (e.g., Blue Fire).
     * This capability acts as a marker to prevent environmental logic from overwriting
     * a hazardous tile until its effect has naturally expired.
     */
    ACTIVE_HAZARD
}
