package game.sanctuary;

import edu.monash.fit2099.engine.positions.Location;
import game.managers.QuotaManager;

/**
 * An interface representing a source of spatial distortion or corruption within the game world.
 *
 * Classes implementing this interface (typically  Ground} types like BlackHolePortal,RageGround, CorruptedSafeHouse
 * represent anomalies that can be interacted with through stabilization or administrative auditing.
 *
 * This interface allows systems such as workers or the SuperComputer to interact with
 * various types of distortions polymorphically, regardless of their specific hazard logic.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public interface DistortionSource {

    /**
     * Stabilizes the distortion, typically restoring the ground to a neutral state (e.g., a Floor).
     *
     * This is usually triggered by a worker performing a specific action to "cleanse"
     * the tile of its corrupted properties.
     *
     * @param location The location of the distortion source.
     */
    void stabilise(Location location);

    /**
     * Performs a Distortion Audit Protocol on the source.
     *
     * Auditing a distortion source typically generates company credits via the
     *  QuotaManager, but often triggers a "flare" or "reaction"—a violent
     * side effect caused by venting the anomaly's energy.
     *
     * @param quotaManager The manager handling company credits and financial goals.
     * @param location     The location of the distortion source being audited.
     * @return A descriptive string detailing the credits gained and any side effects triggered.
     */
    String audit(QuotaManager quotaManager, Location location);
}