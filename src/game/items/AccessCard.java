package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.ItemStatistics;
import game.actions.UnlockDoorAction;
import game.capabilities.Unlockable;
import game.managers.AlarmManager;

/**
 * A security item used to authorize entry through locked doors.
 * The card is essential for navigating the facility. Its functionality is
 * suspended during active facility alarms to prevent unauthorized movement
 * during emergencies (REQ4).
 *
 * @author Jewell Gomes
 */
public class AccessCard extends Item {

    /**
     * Constructor for the Access Card.
     * Initializes the card with a weight of one unit and sets it as portable.
     */
    public AccessCard() {
        super("Access Card", '▤');
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
        this.makePortable();
    }

    /**
     * Generates a list of unlock actions for adjacent doors.
     * Scans surrounding exits for locked structures that implement the unlockable
     * interface. This method returns an empty list if the facility alarm is active.
     * @param owner The actor carrying the access card.
     * @param map The game map containing the actor.
     * @return A collection of valid unlock door actions.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (AlarmManager.getInstance().isActive()) {
            return actions;
        }

        Location currentLocation = map.locationOf(owner);

        for (Exit exit : currentLocation.getExits()) {
            Location destination = exit.getDestination();

            Unlockable target = destination.getGroundAs(Unlockable.class);
            if (target != null && !target.isUnlocked()) {
                actions.add(new UnlockDoorAction(target, exit.getName()));
            }
        }
        return actions;
    }
}
