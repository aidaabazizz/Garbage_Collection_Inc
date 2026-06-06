package game.capabilities;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import java.util.Optional;

/**
 * A specialized status representing an active biological infection.
 * This class implements Requirement 4, where an infected host suffers from
 * recurring health damage and triggers specific host-dependent behaviors
 * (such as oil drainage or parasite spawning) during every game turn.
 *
 * @author Chathya Attanayake
 * @version 1.0
 */
public class InfectionStatus extends DamageOverTimeStatus {
    /**
     * Constructor for InfectionStatus.
     * Initializes the infection with an indefinite duration, as per Requirement 4,
     * where the infection persists until the host is destroyed or falls unconscious.
     */
    public InfectionStatus() {
        super("Infection", Integer.MAX_VALUE);
    }

    /**
     * Updates the infection logic every game round.
     * This method handles the primary damage-over-time effect and delegates
     * host-specific updates to the entity if it implements the Infectable interface.
     *
     * @param entity   The GameEntity (Actor or Item) currently affected by the infection.
     * @param location The current location of the entity.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        // 1. Handle health damage (1 HP) via parent class
        super.tickStatus(entity, location);

        // 2. Check for Infectable capability using traditional Optional check
        // Check if the entity has the Infectable capability
        Optional<Infectable> maybeHost = entity.asCapability(Infectable.class);

        if (maybeHost.isPresent()) {
            Infectable host = maybeHost.get();
            // Trigger the unique turn-based update for this specific host type
            host.updateInfection(location);
        }
    }

    /**
     * Returns a string representation of the status for display in menus.
     *
     * @return The name of the status.
     */
    @Override
    public String toString() {
        return "Infection";
    }
}