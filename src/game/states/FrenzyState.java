package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.HuntBehaviour;
import game.behaviours.AttackBehaviour;
import game.capabilities.BeakMutable;
import game.capabilities.Consumable;
import game.enums.Ability;
import game.enums.ChickenState;
import game.weapons.CrazyChickenBeak;

/**
 * FRENZY STATE for CrazyChicken.
 *
 * The chicken furiously chases and attacks the nearest worker.
 * Deals half damage but attacks with increased frequency.
 *
 * Transitions (deterministic, no randomness):
 * - After 3 turns: if adjacent worker has consumable → HUNGRY
 * - After 3 turns: if no adjacent consumable → WANDER
 * - Otherwise: stay in FRENZY
 *
 * @author Aida
 * @version 1.0
 */
public class FrenzyState implements State<ChickenState> {

    private static final int FRENZY_DURATION = 3;
    private static final int SHOCKWAVE_RADIUS = 8;
    private static final int PUSH_BACK_DISTANCE = 2;
    private static final int SHOCKWAVE_DAMAGE = 2;

    private final HuntBehaviour huntBehaviour;
    private final AttackBehaviour attackBehaviour;
    private final Display display;

    private int originalDamage;
    private int originalHitRate;

    /**
     * Frenzy beak that deals half damage with double hit rate.
     */
    private static class FrenzyBeak extends CrazyChickenBeak {
        public FrenzyBeak(int originalDamage, int originalHitRate) {
            super(originalDamage / 2, originalHitRate * 2, "frenzy pecks");
        }
    }

    /**
     * Constructor with dependency injection for testability (DIP).
     */
    public FrenzyState() {
        this(new HuntBehaviour(), new AttackBehaviour(), new Display());
    }

    /**
     * Constructor for dependency injection.
     */
    public FrenzyState(HuntBehaviour huntBehaviour, AttackBehaviour attackBehaviour, Display display) {
        this.huntBehaviour = huntBehaviour;
        this.attackBehaviour = attackBehaviour;
        this.display = display;
    }

    @Override
    public Action getAction(Actor actor, Location location) {
        // Priority 1: Attack adjacent workers
        Action attackAction = attackBehaviour.operate(actor, location);
        if (attackAction != null) {
            return attackAction;
        }

        // Priority 2: Hunt towards nearest worker
        Action huntAction = huntBehaviour.operate(actor, location);
        if (huntAction != null) {
            return huntAction;
        }

        // Fallback: Wander
        return new WanderingChicken().getAction(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {

        // FIRST: Check if frenzy duration has expired (after 3 turns)
        if (turnsInCurrentState >= FRENZY_DURATION) {

            // SECOND: If expired, check for adjacent worker with consumable
            if (hasAdjacentWorkerWithConsumable(location)) {
                return ChickenState.HUNGRY;
            }

            // THIRD: No consumable available - return to wandering
            return ChickenState.WANDER;
        }

        // STAY in current state
        return ChickenState.FRENZY;
    }

    /**
     * Checks if any adjacent worker has a consumable item in inventory.
     *
     *
     * @param location The chicken's current location
     * @return true if adjacent worker has consumable, false otherwise
     */
    private boolean hasAdjacentWorkerWithConsumable(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();

            if (!destination.containsAnActor()) {
                continue;
            }

            Actor target = destination.getActor();

            if (!target.hasAbility(Ability.WORKER)) {
                continue;
            }

            for (Item item : target.getInventory().getItems()) {
                if (item.asCapability(Consumable.class).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        BeakMutable beakMutable = (BeakMutable) actor;

        CrazyChickenBeak originalBeak = beakMutable.getBeak();
        this.originalDamage = originalBeak.getDamageValue();
        this.originalHitRate = originalBeak.getHitRateValue();

        beakMutable.setBeak(new FrenzyBeak(originalDamage, originalHitRate));

        // Frenzied screech shockwave
        display.println("\u001B[33m" + actor + " lets out a FRENZIED SCREECH! The ground shakes!\u001B[0m");

        GameMap map = location.map();

        // Iterate through all map tiles
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);

                if (!targetLoc.containsAnActor()) {
                    continue;
                }

                Actor target = targetLoc.getActor();

                // Check if target is a worker
                if (!target.hasAbility(Ability.WORKER)) {
                    continue;
                }

                int distance = Math.abs(x - location.x()) + Math.abs(y - location.y());

                if (distance <= SHOCKWAVE_RADIUS) {
                    // Apply damage
                    target.hurt(SHOCKWAVE_DAMAGE);
                    display.println("\u001B[31m" + target + " takes " + SHOCKWAVE_DAMAGE +
                            " damage from the shockwave! (HP: " + getCurrentHealth(target) +
                            "/" + getMaxHealth(target) + ")\u001B[0m");

                    // Check if worker died using isConscious()
                    if (!target.isConscious()) {
                        display.println("\u001B[31m" + target + " has been killed by the shockwave!\u001B[0m");
                    }

                    // Push worker away from chicken
                    pushBack(target, targetLoc, location, map);
                }
            }
        }
    }

    /**
     * Pushes a target actor away from the source location.
     * Uses Manhattan direction calculation.
     *
     * @param target The actor being pushed
     * @param targetLoc Current location of target
     * @param sourceLoc Source location (chicken)
     * @param map The game map
     */
    private void pushBack(Actor target, Location targetLoc, Location sourceLoc, GameMap map) {
        int dx = targetLoc.x() - sourceLoc.x();
        int dy = targetLoc.y() - sourceLoc.y();

        if (dx != 0) dx = dx > 0 ? 1 : -1;
        if (dy != 0) dy = dy > 0 ? 1 : -1;

        Location current = targetLoc;

        for (int step = 0; step < PUSH_BACK_DISTANCE; step++) {
            int newX = current.x() + dx;
            int newY = current.y() + dy;

            if (!map.getXRange().contains(newX) || !map.getYRange().contains(newY)) {
                display.println(target + " cannot be pushed further (map boundary)!");
                return;
            }

            Location next = map.at(newX, newY);

            if (next.containsAnActor() || !next.canActorEnter(target)) {
                display.println(target + " is knocked back but hits an obstacle!");
                return;
            }

            map.moveActor(target, next);
            display.println(target + " is pushed back to " + next + "!");
            current = next;
        }
    }

    /**
     * Gets current health using engine's statistics system.
     */
    private int getCurrentHealth(Actor actor) {
        return actor.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
    }

    /**
     * Gets maximum health using engine's statistics system.
     */
    private int getMaxHealth(Actor actor) {
        return actor.getMaximumStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
    }

    @Override
    public void onExit(Actor actor, Location location) {
        BeakMutable beakMutable = (BeakMutable) actor;
        beakMutable.setBeak(new CrazyChickenBeak(originalDamage, originalHitRate));

        display.println("\u001B[33m" + actor + " calms down from its frenzy.\u001B[0m");
    }

    @Override
    public String getStateName() {
        return "FRENZY";
    }
}