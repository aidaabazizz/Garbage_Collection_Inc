package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ChargeSource;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ShockedStatus;

import java.util.Random;

public class AtmosphericChargeSource extends Ground implements ChargeSource {
    private final Random rand = new Random();
    private static final double LIGHTNING_CHANCE = 0.10;

    public AtmosphericChargeSource() {
        super('⛈', "Atmospheric Controller");
        this.enableAbility(MaterialCapability.ENERGIZED);
    }

    @Override
    public void tick(Location location) {
        if (rand.nextDouble() < LIGHTNING_CHANCE) {
            int maxX = location.map().getXRange().max();
            int maxY = location.map().getYRange().max();
            int strikeX = rand.nextInt(maxX + 1);
            int strikeY = rand.nextInt(maxY + 1);
            Location strikePoint = location.map().at(strikeX,  strikeY);

            Display display = new Display();
            String yellow = "\u001B[33m";
            String reset = "\u001B[0m";
            display.println(yellow + "⛈ A bolt strikes the facility at (" + strikeX + ", " + strikeY + ")!" + reset);
            this.releaseCharge(strikePoint, display, "a massive Lightning Bolt");
        }
    }

    @Override
    public void releaseCharge(Location location, Display display, String sourceName) {
        ChargeReactive ground = location.getGroundAs(ChargeReactive.class);
        if (ground != null) {
            ground.reactToCharge(location, display, sourceName);
        }

        if (location.containsAnActor()) {
            Actor victim = location.getActor();

            victim.hurt(3);
            victim.addStatus(new ShockedStatus(2));


            victim.asCapability(ChargeReactive.class)
                    .ifPresent(actorReactive -> actorReactive.reactToCharge(location, display, sourceName));

            victim.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(location, display, sourceName));
        }
        location.getItemsAs(ChargeReactive.class)
                .forEach(item -> item.reactToCharge(location, display, sourceName));
    }
}
