package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.HuntBehaviour;
import game.behaviours.AttackBehaviour;
import game.enums.Ability;
import game.actors.CrazyChicken;
import game.weapons.CrazyChickenBeak;

/**
 * FRENZY STATE for CrazyChicken.
 * The chicken furiously chases and attacks the nearest worker.
 * Deals half damage but attacks with increased frequency.
 *
 * Transitions to:
 * - WANDER: after 3 rounds (frenzy duration ends)
 * - stays FRENZY: otherwise
 *
 * @author Aida
 */
public class FrenzyState implements State {
    private final HuntBehaviour huntBehaviour = new HuntBehaviour();
    private final AttackBehaviour attackBehaviour = new AttackBehaviour();
    private static final int FRENZY_DURATION = 3;

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

        return new WanderState().getAction(actor, location);
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
        // Store original weapon stats using the protected fields via our getter
        CrazyChickenBeak originalBeak = (CrazyChickenBeak) actor.getIntrinsicWeapon();
        this.originalDamage = originalBeak.getDamageValue();
        this.originalHitRate = originalBeak.getHitRateValue();

        // Replace with frenzy beak
        if (actor instanceof CrazyChicken) {
            ((CrazyChicken) actor).setBeak(new FrenzyBeak(originalDamage, originalHitRate));
        }

        // IMMEDIATE EFFECT: The chicken screeches loudly
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
                            // Damage: 2 HP
                            target.hurt(2);

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
                    current = newLoc;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void onExit(Actor actor, Location location) {
        // Restore original beak
        if (actor instanceof CrazyChicken) {
            ((CrazyChicken) actor).setBeak(new CrazyChickenBeak(originalDamage, originalHitRate));
        }
    }

    @Override
    public String getStateName() {
        return "FRENZY";
    }
}