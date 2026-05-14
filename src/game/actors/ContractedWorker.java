package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.*;
import game.enums.Ability;
import game.managers.AlarmManager;
import edu.monash.fit2099.engine.items.Item;
import game.finance.Wallet;
import game.managers.CreatureSpawner;
import game.actions.DisorientedMoveAction;
import game.managers.Spawner;

import java.util.HashSet;
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
    }

    /**
     * Checks if this worker is currently frozen.
     * Uses class comparison - NOT instanceof, NOT switch.
     * Reliably detects FrozenStatus attached to this actor.
     *
     * @return true if frozen and status is active, false otherwise
     */
    private boolean isFrozen() {
        for (Status status : this.statuses()) {
            if (status.getClass() == FrozenStatus.class && status.isStatusActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this worker is currently disoriented by a blizzard.
     * Uses class comparison - NOT instanceof, NOT switch.
     * Reliably detects BlizzardDisorientationStatus attached to this actor.
     *
     * @return true if disoriented and status is active, false otherwise
     */
    private boolean isDisoriented() {
        for (Status status : this.statuses()) {
            if (status.getClass() == BlizzardDisorientationStatus.class && status.isStatusActive()) {
                return true;
            }
        }
        return false;
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

        // REQ5 Freeze check using class comparison
        if (isFrozen()) {
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

        if (isDisoriented()) {
            ActionList wrappedActions = new ActionList();
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
                    String hotKey = getHotKeyForDirection(moveDirection);
                    wrappedActions.add(new DisorientedMoveAction(moveDirection, hotKey));
                } else if (moveDirection == null) {
                    wrappedActions.add(action);
                }
            }

            Menu menu = new Menu(wrappedActions);
            return menu.showMenu(this, display);
        }

        // return/print the console menu with original actions
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
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
     * Disorients the worker by adding BlizzardDisorientationStatus for the specified duration.
     * Called when Elsa enters BLIZZARD state.
     *
     * @param duration The number of turns the worker remains disoriented.
     */
    @Override
    public void disorient(int duration) {
        this.addStatus(new BlizzardDisorientationStatus(duration));
    }
}