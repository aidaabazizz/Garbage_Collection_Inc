package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.capabilities.KillerInstinctStatus;
import game.managers.QuotaManager;
import game.sanctuary.DistortionSource;
import java.util.Random;

public class RageGround extends Ground implements DistortionSource {

    private final Random random = new Random();
    private static final int KILLER_INSTINCT_SPAN = 3;

    public RageGround() {
        super('╬', "Rage Ground");
        this.enableAbility(DistortionCapability.CORRUPTED);
    }

    @Override
    public void tick(Location location) {
        Display display = new Display();
        // 1. Trigger relocation only when a Worker steps on it
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            // Only trigger if Bob doesn't already have the status (SOLID Entry Trigger)
            if (!actor.hasStatus(KillerInstinctStatus.class) && actor.hasAbility(Ability.WORKER)) {
                actor.addStatus(new KillerInstinctStatus(KILLER_INSTINCT_SPAN));
                display.println("\u001B[31m>>> " + actor + " triggers the Rage Ground! It will vanish soon...\u001B[0m");
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

    /**
     * Distortion Audit Protocol: The rage ground discharges all its aggression in
     * one final burst — granting KillerInstinct to ALL actors within radius 1,
     * then immediately relocating (its natural lifecycle but triggered early).
     *
     * @param quotaManager the shared quota system
     * @param location     this tile's location
     * @return audit result description
     */
    @Override
    public String audit(QuotaManager quotaManager, Location location) {
        // 1. Contribute credits to REQ1
        String creditMsg = quotaManager.addCompanyCredits(25);

        // 2. Final unstable effect — grant KillerInstinct to all adjacent actors
        StringBuilder effectMsg = new StringBuilder();
        effectMsg.append("The Rage Ground discharges in a violent burst!\n");

        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                Actor target = adj.getActor();
                target.addStatus(new KillerInstinctStatus(KILLER_INSTINCT_SPAN));
                effectMsg.append(target).append(" is consumed by rage energy!\n");
            }
        }

        // 3. Trigger immediate relocation (its natural "weakened" state is gone)
        relocate(location);
        effectMsg.append("The Rage Ground's energy is spent — it has relocated!");

        return creditMsg + "\n" + effectMsg;
    }

}