package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import game.enums.MaterialCapability;

import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract base class for all non-player characters on the moon.
 * It centralizes the behavior-based decision-making logic (Strategy Pattern).
 *
 * High-Voltage Integration (REQ3):
 * This class implements a global "Paralysis Check" within the turn-processing loop.
 * Any NPC affected by a high-voltage hazard will automatically skip its turn,
 * ensuring that galvanic effects are enforced consistently across all enemy types.
 *
 * @author Jewell Gomes
 */
public abstract class NonPlayerCharacter extends Actor {
    /**
     * A map of behaviors that the NPC can perform.
     * The key (Integer) represents the priority of the behavior, where a lower
     * integer value indicates a higher priority. A TreeMap is used to ensure
     * behaviors are iterated in ascending order of priority.
     */
    protected final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructor for the Non Player Character class.
     */
    public NonPlayerCharacter(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
    }

    /**
     * Helper method to detect if the NPC is currently affected by paralysis.
     * This checks for the {@link MaterialCapability#PARALYZED} capability.
     *
     * @return true if the NPC is paralyzed and should skip its turn; false otherwise.
     */
    private boolean isParalyzed() {
        return this.hasAbility(MaterialCapability.PARALYZED);
    }


    /**
     * Iterates through the behaviors in priority order and returns the first valid action.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (this.isParalyzed()) {
            display.println("\u001B[33m" + this + " is paralyzed by the electric charge and cannot move!\u001B[0m");
            return new DoNothingAction();
        }
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) return action;
        }
        return new DoNothingAction();
    }
}