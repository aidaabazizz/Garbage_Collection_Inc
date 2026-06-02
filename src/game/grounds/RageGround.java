package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.capabilities.KillerInstinctStatus;
import game.sanctuary.DistortionSource;
import java.util.Random;

public class RageGround extends Ground implements DistortionSource {

    private int relocationTimer = -1;
    private final Random random = new Random();

    public RageGround() {
        super('╬', "Rage Ground");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        Display display = new Display();
        // 1. Trigger relocation only when a Worker steps on it
        if (location.containsAnActor() && relocationTimer == -1) {
            Actor actor = location.getActor();
            // Only trigger if Bob doesn't already have the status (SOLID Entry Trigger)
            if (!actor.hasStatus(KillerInstinctStatus.class)) {
                actor.addStatus(new KillerInstinctStatus(3));
                display.println("\u001B[31m>>> " + actor + " triggers the Rage Ground! It will vanish soon...\u001B[0m");
                relocationTimer = 4; // Start the 3-turn death clock
            }
        }

        // 2. Handle Relocation Countdown
        if (relocationTimer > 0) {
            relocationTimer--;
            if (relocationTimer == 0) {
                relocate(location);
            }
        }
    }

    private void relocate(Location currentLocation) {
        GameMap map = currentLocation.map();
        currentLocation.setGround(new Floor()); // Disappear
        System.out.println("\u001B[31m>>> The Rage Ground has vanished and relocated!\u001B[0m");

        int x, y;
        Location newLoc;
        do {
            x = random.nextInt(map.getXRange().max());
            y = random.nextInt(map.getYRange().max());
            newLoc = map.at(x, y);
            // FIX: Ensure we don't spawn under an actor (prevents infinite loops)
        } while (!newLoc.getGround().canActorEnter(null) ||
                newLoc.containsAnActor() ||
                newLoc.getGround().hasAbility(DistortionCapability.CORRUPTED));

        newLoc.setGround(new RageGround());
    }

    @Override
    public String releaseDistortion(Actor a, GameMap m, Location l) {
        return "";
    }

    @Override
    public String stabilise(Location location) {
        location.setGround(new Floor());

        // Remove KillerInstinctStatus from any worker on this tile
        // Uses statuses() list scan — no instanceof, uses class comparison (DIP)
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            if (actor.hasAbility(Ability.WORKER)) {
                for (Status status : actor.statuses()) {
                    if (status.getClass() == KillerInstinctStatus.class) {
                        actor.removeStatus(status);
                        break;
                    }
                }
            }
        }

        return "The Rage Ground has been neutralized — Killer Instinct suppressed!";
    }
}