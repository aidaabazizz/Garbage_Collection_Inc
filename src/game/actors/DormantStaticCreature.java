package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.WanderBehaviour;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import game.inventory.BasicInventory;

public class DormantStaticCreature extends NonPlayerCharacter implements ChargeReactive {
    /** Priority level for the wandering behavior. */
    private static final int INITIAL_HEALTH = 10;

    public DormantStaticCreature() {
        super("Dormant Static Creature", 'O', INITIAL_HEALTH, new BasicInventory());
    }

    /**
     * FIXED: First parameter must be ActionList to override super.playTurn.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        // Rule 2 Complexity: Actor-to-Ground interaction
        if (here.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            display.println(this + " absorbs charge from the ground!");

            String sourceName = "the " + here.getGround() + " beneath its feet";
            // Call the interface method using the display provided by the engine
            this.reactToCharge(here, display, sourceName);

            return new DoNothingAction();
        }
        return new DoNothingAction();
    }

    /**
     * FIXED: Matches your specific ChargeReactive interface.
     * FIXED: Handled GameEngineException for spawning.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        display.println("\u001B[33m!!! The " + this + " is stimulated by " + sourceName + " and shatters !!!\u001B[0m");
        display.println("\u001B[33m>>> A Static Stalker has been born!\u001B[0m");
        location.map().removeActor(this);
        try {
            location.map().addActor(new StaticStalker(), location);
        } catch (Exception e) {
            display.println("Evolution failed: Tile at " + location + " is blocked.");
        }
    }
}