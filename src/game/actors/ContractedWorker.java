package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.InvertedMoveAction;
import game.actions.RageStrikeAction;
import game.capabilities.*;
import game.enums.Ability;
import game.managers.AlarmManager;
import edu.monash.fit2099.engine.items.Item;
import game.finance.Wallet;
import game.managers.CreatureSpawner;
import game.actions.DisorientedMoveAction;
import game.managers.Spawner;
import game.sanctuary.DamageInterceptor;
import game.utils.SpatialSearch;
import game.weapons.WorkerFists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The primary player-controlled actor representing a contracted worker.
 * This class implements the logic for human-controlled entities stationed
 * at the moon facility. It possesses specific worker capabilities required
 * to interact with facility security systems and handle hazardous materials
 * under the employment of Garbage Collection Inc.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (Modified by)
 * @author Aida (Modified by)
 */
public class ContractedWorker extends Actor implements Infectable, Freezable, Disorientable {
    /** The number of turns elapsed since the last parasite spawn. */
    private int spawnCounter = 0;
    /** The fixed interval at which a new parasite is spawned while infected. */
    private static final int SPAWN_THRESHOLD = 5;
    /** The spawning service used to handle creature creation and side effects. */
    private final Spawner spawner;

    /**
     * Constructor to initialize the worker with their starting statistics.
     *
     * @param name        The display name of the worker.
     * @param displayChar The character representing the worker on the map.
     * @param hitPoints   The initial health points of the worker.
     * @param inventory   The inventory system assigned to the worker.
     * @param spawner     The spawner used for creating parasites when infected.
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory, Spawner spawner) {
        super(name, displayChar, hitPoints, inventory);
        this.spawner = spawner;
        this.enableAbility(Ability.WORKER);
        this.setIntrinsicWeapon(new WorkerFists());

    }



    /**
     * Orchestrates the worker's turn by processing environmental status and user input.
     * The method executes the following sequence:
     * 1. Notifies the user of global facility states such as active lockdowns.
     * 2. Validates the consciousness of the actor to determine if a turn can be taken.
     * 3. Displays all active status effects and inventory notifications to the user interface.
     * 4. Checks for frozen status - if frozen, skips turn completely.
     * 5. Resolves multi-turn actions or displays a selection menu for player interaction.
     * 6. Wraps movement actions with disoriented versions if blizzard status is active.
     *
     * @param actions    A collection of available actions provided by the engine.
     * @param lastAction The action performed in the previous turn.
     * @param map        The current game map the worker is navigating.
     * @param display    The terminal interface for outputting messages and menus.
     * @return The Action selected by the player or the next part of a multi-turn action.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        display.endLine();
        // Check global facility state
        if (AlarmManager.getInstance().isActive()) {
            display.println("\u001B[31m" + "!!! RED ALERT: FACILITY LOCKED DOWN !!!" + "\u001B[0m");
        }

        // Validate consciousness
        if (!this.isConscious()) {
            String deathMessage = this.unconscious(map);
            display.println(deathMessage);
            return new DoNothingAction();
        }
        // Output current environmental or internal status effects
        for (Status status : this.statuses()) {
            display.println(this.name + " is affected by: " + status.toString());
        }

        if (this.hasStatus(FrozenStatus.class)) {
            display.println("\u001B[36m" + this + " is frozen solid! Cannot take any actions until the ice melts!\u001B[0m");
            return new DoNothingAction();
        }

        for (UpdateNotifier notifier : this.getInventory().getItemsAs(UpdateNotifier.class)) {
            String msg = notifier.updateMessage();
            if (msg != null) {
                display.println("!!! " + msg);
            }
        }

        // Handle multi-turn Actions
        if (lastAction != null && lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        // 3. REQ4: Killer Mode targeting (Using your SpatialSearch utility)
        if (this.hasStatus(KillerInstinctStatus.class)) {
            List<Actor> targets = SpatialSearch.getActorsWithinDistance(map.locationOf(this), 3);
            for (Actor target : targets) {
                if (target != this) actions.add(new RageStrikeAction(target, "range", getIntrinsicWeapon()));
            }
        }

        // 4. Movement Logic (The "Correct" way to handle both A2 and A3)
        ActionList processedActions = actions;

        // REQ4: Portals show "Inverted" labels in the menu
        if (this.hasStatus(ReversedMovementStatus.class)) {
            display.println("\u001B[35mSpatial anomaly detected: Movement is inverted!\u001B[0m");
            processedActions = processInvertedActions(actions, map);
        }
        // REQ5: Blizzard uses "Sneaky" labels (looks like normal movement)
        else if (this.hasStatus(BlizzardDisorientationStatus.class)) {
            display.println("\u001B[36mThe blizzard is blinding! You feel disoriented...\u001B[0m");
            processedActions = processDisorientedActions(actions);
        }

        return new Menu(processedActions).showMenu(this, display);
    }

    /**
     * REQ4 Helper: Scans a 3-tile radius and injects RageStrikeAction for every actor found.
     */
    private void injectRageStrikeActions(ActionList actions, GameMap map) {
        Location here = map.locationOf(this);

        // USE UTILITY: Get all actors in a 3-tile radius
       List<Actor> targets = SpatialSearch.getActorsWithinDistance(here, 3);

        for (Actor target : targets) {
            // Only inject if the target is NOT me
            if (target != this) {
                actions.add(new RageStrikeAction(target, "within range", this.getIntrinsicWeapon()));
            }
        }
    }

    /**
     * REQ4 Helper: Wraps movement actions with InvertedMoveAction.
     */
    private ActionList processInvertedActions(ActionList actions, GameMap map) {
        ActionList inverted = new ActionList();
        for (Action action : actions.getUnmodifiableActionList()) {
            inverted.add(wrapIfMovementInverted(action, map));
        }
        return inverted;
    }

    /**
     * REQ5 Helper: Wraps movement actions with DisorientedMoveAction (Blizzard).
     */
    private ActionList processDisorientedActions(ActionList actions) {
        ActionList wrapped = new ActionList();
        Set<String> addedDirections = new HashSet<>();
        String[] allDirections = {"North", "South", "East", "West", "North-East", "South-East", "South-West", "North-West"};

        for (Action action : actions.getUnmodifiableActionList()) {
            String description = action.menuDescription(this);
            String moveDirection = null;

            for (String dir : allDirections) {
                if (description.contains(dir)) {
                    moveDirection = dir;
                    break;
                }
            }
            if (moveDirection != null && !addedDirections.contains(moveDirection)) {
                addedDirections.add(moveDirection);
                wrapped.add(new DisorientedMoveAction(moveDirection, getHotKeyForDirection(moveDirection)));
            } else if (moveDirection == null) {
                wrapped.add(action);
            }
        }
        return wrapped;
    }

    /**
     * Returns the hotkey associated with a given movement direction.
     * Used by the blizzard state to preserve hotkey functionality when
     * replacing movement actions with disoriented versions.
     * Uses array mapping - NO switch!
     *
     * @param direction The movement direction (North, South, East, West, etc.)
     * @return The hotkey character or empty string if not found
     */
    private String getHotKeyForDirection(String direction) {
        String[] directions = {"North", "South", "East", "West", "North-East", "South-East", "South-West", "North-West"};
        String[] hotKeys = {"8", "2", "6", "4", "9", "3", "1", "7"};
        for (int i = 0; i < directions.length; i++) {
            if (directions[i].equals(direction)) {
                return hotKeys[i];
            }
        }
        return "";
    }


    @Override
    public void hurt(int points) {
        if (this.hasAbility(DamageInterceptor.PROTECTED)) {
            points = points / 2;
        }
        super.hurt(points);
    }

    /**
     * REQ4 Helper: Identifies movement actions and flips them.
     * This follows SRP by keeping coordinate math out of playTurn.
     */
    private Action wrapIfMovementInverted(Action action, GameMap map) {
        // 1. Identify if the action is a movement action
        // We use the menuDescription to find the direction snippet
        String desc = action.menuDescription(this);
        String moveSnippet = " moves ";
        int moveIndex = desc.indexOf(moveSnippet);

        // If " moves " isn't in the description, it's not a move action we care about
        if (moveIndex == -1) return action;

        // Extract just the direction part (e.g., "North-East")
        String actionDirection = desc.substring(moveIndex + moveSnippet.length());

        Location here = map.locationOf(this);

        // 2. Scan exits for an EXACT match
        for (Exit exit : here.getExits()) {
            // Use .equals() instead of .contains() to prevent "North" matching "North-East"
            if (actionDirection.equals(exit.getName())) {
                Location intendedDest = exit.getDestination();

                // Calculate the Mirror Image vector
                int dx = intendedDest.x() - here.x();
                int dy = intendedDest.y() - here.y();

                int flippedX = here.x() - dx;
                int flippedY = here.y() - dy;

                if (map.getXRange().contains(flippedX) && map.getYRange().contains(flippedY)) {
                    Location flippedDest = map.at(flippedX, flippedY);

                    // Return the Inverted Action with the original label and hotkey
                    // This creates the "Highjacked Input" effect
                    return new InvertedMoveAction(flippedDest, exit.getName(), exit.getHotKey());
                }
            }
        }

        // Fallback: if we couldn't find a matching exit, return the original action
        return action;
    }

    /**
     * Responds to the initial contact with a Parasite.
     * Requirement 4: The worker becomes a living hive and gains the Infection status.
     *
     * @param location The location where the infection occurred.
     */
    @Override
    public void reactToInfection(Location location) {
        this.addStatus(new InfectionStatus());
    }

    /**
     * Handles the ongoing effects of the infection every turn.
     * Per Requirement 4, a new Parasite is spawned on an adjacent tile every 5 turns.
     *
     * @param location The worker's current location.
     */
    @Override
    public void updateInfection(Location location) {
        spawnCounter++;
        if (spawnCounter >= SPAWN_THRESHOLD) {
            spawnCounter = 0;
            //REQ4: Force the worker to spawn a new Parasite on an adjacent tile
            this.spawner.spawnParasite(location);
        }
    }

    /**
     * Freezes the worker by adding FrozenStatus for the specified duration.
     * Called when Elsa enters FREEZE state.
     *
     * @param duration The number of turns the worker remains frozen.
     */
    @Override
    public void freeze(int duration) {
        this.addStatus(new FrozenStatus(duration));
    }

    /**
     * Disorients the worker by adding ReversedMovementStatus for the specified duration.
     * Called when Elsa enters BLIZZARD state.
     *
     * @param duration The number of turns the worker remains disoriented.
     */
    @Override
    public void disorient(int duration) {
        this.addStatus(new BlizzardDisorientationStatus(duration));
    }
}