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
import game.capabilities.Ability;
import game.capabilities.UpdateNotifier;
import game.managers.AlarmManager;

/**
 * The primary player-controlled actor representing a contracted worker.
 * This class implements the logic for human-controlled entities stationed
 * at the moon facility. It possesses specific worker capabilities required
 * to interact with facility security systems and handle hazardous materials
 * under the employment of Garbage Collection Inc.
 *
 * @author Jewell Gomes
 */
public class ContractedWorker extends  Actor {

    /**
     * Constructor to initialize the worker with their starting statistics.
     *
     * @param name The display name of the worker.
     * @param displayChar The character representing the worker on the map.
     * @param hitPoints The initial health points of the worker.
     * @param inventory The inventory system assigned to the worker.
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
     * @param actions A collection of available actions provided by the engine.
     * @param lastAction The action performed in the previous turn.
     * @param map The current game map the worker is navigating.
     * @param display The terminal interface for outputting messages and menus.
     * @return The Action selected by the player or the next part of a multi-turn action.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {

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

        // return/print the console menu
        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}
