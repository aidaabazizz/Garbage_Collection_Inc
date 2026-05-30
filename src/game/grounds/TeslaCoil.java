package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.GalvanicSurgeAction;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ShockedStatus;

public class TeslaCoil extends Ground implements ChargeSource, ChargeReactive {
    private int turnsToCharge = 3;
    private static final String PURPLE = "\u001B[35m";
    private static final String RESET = "\u001B[0m";
    public TeslaCoil() {
        super('Ꮺ', "TeslaCoil");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    @Override
    public void tick(Location location) {
        Display display = new Display();
        this.turnsToCharge--;
        if (this.turnsToCharge > 0) {
            display.println(PURPLE + "Tesla Coil is humming... (Charge level: " + (3 - this.turnsToCharge) + "/3)" + RESET);
        } else {
            display.println(PURPLE + "The Tesla Coil reaches critical mass and discharges a massive pulse!" + RESET);
            this.releaseCharge(location, display, "the Tesla Coil Pulse");
        }
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        actions.add(new GalvanicSurgeAction(this, "Tesla Coil", location));
        return actions;
    }

    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        display.println("\u001B[35m!!! The Tesla Coil is overloaded by " + sourceName + " and discharges !!!\u001B[0m");
        this.releaseCharge(location, display, "the Tesla Coil Pulse");
    }

    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        this.turnsToCharge = 3; // Reset the capacitor

        // manual scan for Manhattan Radius 2 (Diamond Shape)
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                // Formula: |relX| + |relY| <= Radius
                if (Math.abs(x) + Math.abs(y) <= 2) {
                    int targetX = location.x() + x;
                    int targetY = location.y() + y;

                    // Boundary check to prevent crashing at map edges
                    if (location.map().getXRange().contains(targetX) &&
                            location.map().getYRange().contains(targetY)) {

                        Location targetLoc = location.map().at(targetX, targetY);
                        triggerChainReaction(targetLoc, display, sourceName);
                    }
                }
            }
        }
    }

    private void triggerChainReaction(Location target, Display display, String sourceName) {
        // 1. DIP: Trigger ground reactions (e.g., Morphing a Puddle)
        if (!target.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            ChargeReactive groundReactive = target.getGroundAs(ChargeReactive.class);
            if (groundReactive != null) {
                groundReactive.reactToCharge(target, display, sourceName);
            }
        }

        // 2. DIP: Trigger Actor and Inventory reactions
        if (target.containsAnActor()) {
            Actor victim = target.getActor();

            display.println(PURPLE + "⚡ " + victim + " is caught in " + sourceName + "!" + RESET);
            // Effect A: Damage and Status
            victim.hurt(3);
            victim.addStatus(new ShockedStatus(2));

            // Effect B: Evolutionary Trigger (Dormant -> Stalker)
            victim.asCapability(ChargeReactive.class).ifPresent(r -> r.reactToCharge(target, display, sourceName));

            // Effect C: Inventory Trigger (Magnetize Wallet)
            victim.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(r -> r.reactToCharge(target, display, sourceName));
        }
    }
}
