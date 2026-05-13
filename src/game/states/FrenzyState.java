package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.HuntBehaviour;
import game.behaviours.AttackBehaviour;
import game.capabilities.StatefulActor;
import game.enums.Ability;
import game.enums.ChickenState;
import game.weapons.CrazyChickenBeak;

/**
 * FRENZY STATE for CrazyChicken.
 * The chicken furiously chases and attacks the nearest worker.
 * Deals half damage but attacks with increased frequency.
 * Transitions to:
 * - WANDER: after 3 rounds (frenzy duration ends)
 * - stays FRENZY: otherwise
 *
 * @author Aida
 */
public class FrenzyState implements State<ChickenState> {
    private final HuntBehaviour huntBehaviour = new HuntBehaviour();
    private final AttackBehaviour attackBehaviour = new AttackBehaviour();
    private static final int FRENZY_DURATION = 3;
    private final Display display = new Display();

    // Store original damage/hit rate for restoration
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

    @Override
    public Action getAction(Actor actor, Location location) {
        // First try to attack adjacent workers
        Action attackAction = attackBehaviour.operate(actor, location);
        if (attackAction != null) {
            return attackAction;
        }

        // Otherwise hunt towards nearest worker
        Action huntAction = huntBehaviour.operate(actor, location);
        if (huntAction != null) {
            return huntAction;
        }

        return new WanderingChicken().getAction(actor, location);
    }

    @Override
    public ChickenState getNextState(Actor actor, Location location, int turnsInCurrentState) {
        // Frenzy lasts exactly 3 rounds, then return to wander
        if (turnsInCurrentState >= FRENZY_DURATION) {
            return ChickenState.WANDER;
        }
        return ChickenState.FRENZY;
    }

    @Override
    public void onEnter(Actor actor, Location location) {
        // Cast to the interface - no instanceof needed!
        // The state machine guarantees this actor implements StatefulActor
        StatefulActor statefulActor = (StatefulActor) actor;

        // Store original weapon stats using the interface methods
        CrazyChickenBeak originalBeak = statefulActor.getBeak();
        this.originalDamage = originalBeak.getDamageValue();
        this.originalHitRate = originalBeak.getHitRateValue();

        // Replace with frenzy beak using the interface
        statefulActor.setBeak(new FrenzyBeak(originalDamage, originalHitRate));
        statefulActor.setCurrentStateName("FRENZY");

        // IMMEDIATE EFFECT: The chicken screeches loudly
        display.println("\u001B[33m" + actor + " lets out a FRENZIED SCREECH! The ground shakes!\u001B[0m");

        // All workers within 8 tiles take 2 damage and are pushed back 2 tiles
        GameMap map = location.map();

        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location targetLoc = map.at(x, y);
                if (targetLoc.containsAnActor()) {
                    Actor target = targetLoc.getActor();
                    if (target.hasAbility(Ability.WORKER)) {
                        int dist = Math.abs(x - location.x()) + Math.abs(y - location.y());
                        if (dist <= 8) {
                            // Damage: 2 HP with message
                            target.hurt(2);
                            display.println("\u001B[31m" + target + " takes 2 damage from the shockwave!" +
                                    " (" + target + " HP: " + getCurrentHealth(target) + "/" + getMaxHealth(target) + ")\u001B[0m");

                            // Check if worker died
                            if (!target.isConscious()) {
                                display.println("\u001B[31m" + target + " has been killed by the shockwave!\u001B[0m");
                            }

                            // Push back: find a tile away from chicken and move the worker
                            tryPushBack(target, targetLoc, location, map);
                        }
                    }
                }
            }
        }
    }

    private void tryPushBack(Actor target, Location targetLoc, Location chickenLoc, GameMap map) {
        // Calculate direction away from chicken
        int dx = targetLoc.x() - chickenLoc.x();
        int dy = targetLoc.y() - chickenLoc.y();

        // Normalize direction (move away)
        if (dx != 0) dx = dx > 0 ? 1 : -1;
        if (dy != 0) dy = dy > 0 ? 1 : -1;

        // Try to move 2 tiles away
        Location current = targetLoc;
        for (int i = 0; i < 2; i++) {
            int newX = current.x() + dx;
            int newY = current.y() + dy;
            if (map.getXRange().contains(newX) && map.getYRange().contains(newY)) {
                Location newLoc = map.at(newX, newY);
                if (!newLoc.containsAnActor() && newLoc.canActorEnter(target)) {
                    current.map().moveActor(target, newLoc);
                    display.println(target + " is pushed back to " + newLoc + "!");
                    current = newLoc;
                } else {
                    display.println(target + " is knocked back but hits an obstacle!");
                    break;
                }
            } else {
                display.println(target + " cannot be pushed further (map boundary)!");
                break;
            }
        }
    }

    private int getCurrentHealth(Actor actor) {
        return actor.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
    }

    private int getMaxHealth(Actor actor) {
        return actor.getMaximumStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // Restore original beak using the interface
        StatefulActor statefulActor = (StatefulActor) actor;
        statefulActor.setBeak(new CrazyChickenBeak(originalDamage, originalHitRate));
        statefulActor.setCurrentStateName("WANDER");

        display.println("\u001B[33m" + actor + " calms down from its frenzy.\u001B[0m");
    }

    @Override
    public String getStateName() {
        return "FRENZY";
    }
}