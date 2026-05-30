package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.DamageOverTimeStatus;

public class ShockedStatus extends DamageOverTimeStatus {
    public ShockedStatus(int turns) {
        super("Shocked", turns); // normally just 2 turns of shocked
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        super.tickStatus(entity, location);
        Display display = new Display();
        String conduitName = entity.toString() + "'s electric conduit";

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();

            // 1. ZAP THE GROUND (Safe from infinite loops)
            if (!adj.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
                ChargeReactive groundReactive = adj.getGroundAs(ChargeReactive.class);
                if (groundReactive != null) {
                    groundReactive.reactToCharge(adj, display, conduitName);
                }
            }

            // 2. ZAP THE ACTOR (PDF Page 4: "He zaps them")
            if (adj.containsAnActor()) {
                Actor neighbor = adj.getActor();

                // Effect A: Damage (Strictly followed)
                neighbor.hurt(1);

                neighbor.asCapability(ChargeReactive.class)
                        .ifPresent(actorReactive -> actorReactive.reactToCharge(adj, display, conduitName));
            }

            // 3. ZAP ITEMS ON THE GROUND (PDF Page 4: "Energy Pulse")
            // If Bob stands next to a dropped Wallet, he should magnetize it!
            adj.getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(adj, display, conduitName));
        }
    }
}
