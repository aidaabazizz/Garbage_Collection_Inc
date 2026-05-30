package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ParalyzedStatus;
import game.highvoltage.ShockedStatus;

public class ElectrifiedPuddle extends Ground  implements ChargeReactive {
    private int lifeSpan = 8;
    private static final double PARALYZED_CHANCE = 0.20;

    public ElectrifiedPuddle() {
        super('☠', "Electrified Puddle");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        // Refresh the lifespan if struck again!
        display.println("The electrified puddle is surged by " + sourceName + " and its energy is refreshed!");
        this.lifeSpan = 8;
    }

    @Override
    public void tick(Location location) {
        lifeSpan--;

        if (lifeSpan <= 0) {
            location.setGround(new Puddle());
            return;
        }

        if (location.containsAnActor()) {
            Actor victim = location.getActor();
            victim.hurt(1);
            victim.addStatus(new ShockedStatus(2));
        }

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                if (Math.random() < PARALYZED_CHANCE) {
                    adj.getActor().addStatus(new ParalyzedStatus(1));
                }
            }
        }
    }
}
