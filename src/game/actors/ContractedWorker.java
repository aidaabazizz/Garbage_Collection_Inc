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
import game.actions.MovementActionWrapper;
import game.actions.DisorientedMoveAction;
import game.capabilities.DisorientedCapability;

/**
 * The primary player-controlled actor representing a contracted worker.
 * This class implements the logic for human-controlled entities stationed
 * at the moon facility. It possesses specific worker capabilities required
 * to interact with facility security systems and handle hazardous materials
 * under the employment of Garbage Collection Inc.
 *
 * @author Jewell Gomes
 * @author Chathya Attanayake (Modified by)
 */
public class ContractedWorker extends Actor implements Infectable, Freezable, Disorientable {
    private int spawnCounter = 0;
    private static final int SPAWN_THRESHOLD = 5;


    /**
     * Constructor to initialize the worker with their starting statistics.
     *
     * @param name        The display name of the worker.
     * @param displayChar The character representing the worker on the map.
     * @param hitPoints   The initial health points of the worker.
     * @param inventory   The inventory system assigned to the worker.
     */
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(Ability.WORKER);
    }

    /**
     * Orchestrates the worker's turn by processing environmental status and user input.
     * The method executes the following sequence:
     * 1. Notifies the user of global facility states such as active lockdowns.
     * 2. Validates the consciousness of the actor to determine if a turn can be taken.
     * 3. Displays all active status effects and inventory notifications to the user interface.
     * 4. Resolves multi-turn actions or displays a selection menu for player interaction.
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

        // Process background notifications from inventory items
        for (UpdateNotifier notifier : this.getInventory().getItemsAs(UpdateNotifier.class)) {
            String msg = notifier.updateMessage();
            if (msg != null) {
                display.println("!!! " + msg);
            }
        }

        // Handle multi-turn Actions
        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        //REQ5 blizzard state
        boolean isDisoriented = this.asCapability(DisorientedCapability.class)
                .map(DisorientedCapability::isDisoriented)
                .orElse(false);

        if (isDisoriented) {
            actions = MovementActionWrapper.wrapMovementActions(
                    actions,
                    this,
                    this::getHotKeyForDirection,
                    this::extractDirectionFromDescription
            );
        }

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }


    //REQ5 helper methods for BlizzardState
    private String getHotKeyForDirection(String direction) {
        // Using array mapping - NO switch!
        String[] directions = {"North", "South", "East", "West"};
        String[] hotKeys = {"8", "2", "6", "4"};
        for (int i = 0; i < directions.length; i++) {
            if (directions[i].equals(direction)) {
                return hotKeys[i];
            }
        }
        return "";
    }

    private String extractDirectionFromDescription(String description) {
        String[] directions = {"North", "South", "East", "West"};
        for (String dir : directions) {
            if (description.contains(dir)) {
                return dir;
            }
        }
        return "";
    }

    //req 4
    @Override
    public void reactToInfection(Location location) {
        this.addStatus(new InfectionStatus());
    }

    @Override
    public void updateInfection(Location location) {
        spawnCounter++;
        if (spawnCounter >= SPAWN_THRESHOLD) {
            spawnCounter = 0;
            //UPDATED: Delegate spawning to the Spawner.
            // We don't need a manual loop here. The CreatureSpawner's getSpawnLocation
            // will see that the Worker is blocking 'location' and automatically
            // find the adjacent empty tile for the Parasite.
            new CreatureSpawner().spawnParasite(location);
        }
    }

    @Override
    public void freeze(int duration) {
        this.addStatus(new FrozenStatus(duration));
    }

    @Override
    public void disorient(int duration) {
        this.addStatus(new BlizzardDisorientationStatus(duration));
    }

}


