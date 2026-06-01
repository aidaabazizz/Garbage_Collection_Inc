package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.DamageOverTimeStatus;

/**
 * A complex status effect representing an actor being supercharged with galvanic energy.
 *
 * The ShockedStatus is the "Conduit" component of Requirement 3. It utilizes inheritance
 * from {@link DamageOverTimeStatus} to handle host health attrition, while adding
 * unique "Emitter" logic that turns the affected Actor into a mobile power source.
 *
 * Complexity Proof (Rule 2):
 * This class demonstrates "Actor-to-Environment Conduction." Every turn the status is active,
 * the host releases a 1-tile energy pulse that triggers cascading reactions in Grounds,
 * neighboring Actors, and Items on the floor simultaneously.
 *
 * @author Jewell Gomes
 */
public class ShockedStatus extends DamageOverTimeStatus {
    /**
     * Constructor for ShockedStatus.
     *
     * @param turns The duration of the effect. Standard galvanic exposure
     *              typically results in a 2-turn charge.
     */
    public ShockedStatus(int turns) {
        super("Shocked", turns); // normally just 2 turns of shocked
    }

    /**
     * Executes the status logic during the host's tick cycle.
     *
     * This method first invokes the superclass to handle the damage-over-time
     * calculation on the host. It then performs an 8-neighbor scan to propagate
     * electricity into the surrounding environment.
     *
     * @param entity   The actor currently acting as the conduit.
     * @param location The coordinate of the conduit.
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        super.tickStatus(entity, location);
        Display display = new Display();
        String conduitName = entity.toString() + "'s electric conduit";

        /*
         * the "Human Lightning Bolt" AoE Pulse.
         * Bob's body becomes a ChargeSource. We iterate through
         * all exits to simulate energy "leaking" into the adjacent tiles.
         */
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            /*
             * infinite Loop Prevention.
             * We only zap the ground if it isn't already ENERGIZED.
             * This prevents Bob from standing on a Powered Floor and zapping it,
             * which would zap Bob back, creating a StackOverflow crash.
             */
            if (!adj.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
                ChargeReactive groundReactive = adj.getGroundAs(ChargeReactive.class);
                if (groundReactive != null) {
                    groundReactive.reactToCharge(adj, display, conduitName);
                }
            }

            // zap the actor (PDF Page 4: "He zaps them")
            if (adj.containsAnActor()) {
                Actor neighbor = adj.getActor();

                // damage (the "shock" effect)
                neighbor.hurt(1);

                // trigger specific reactions (bob zapping npc's wallet) in future scenarios
                neighbor.asCapability(ChargeReactive.class)
                        .ifPresent(actorReactive -> actorReactive.reactToCharge(adj, display, conduitName));
            }

            /*
             * ZAP ITEMS ON THE GROUND (The "Magnetic Induction" Pulse)
             * If Bob stands next to a dropped Wallet or scrap metal, his body
             * generates enough flux to trigger their reactive properties.
             */
            adj.getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(adj, display, conduitName));
        }
    }
}
