package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.behaviours.AttackBehaviour;
import game.behaviours.HuntBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Ability;
import game.highvoltage.ParalyzedStatus;
import game.inventory.BasicInventory;
import game.weapons.GalvanicStrike;
import game.weapons.UndeadFist;

public class StaticStalker extends NonPlayerCharacter{
    private static final int STRIKE_DAMAGE = 5;
    private static final int STRIKE_HIT_RATE = 75;
    /** Priority level for the wandering behavior. */
    private static final int WANDER_PRIORITY = 999;
    /** Priority level for the attacking behavior. */
    private static final int ATTACK_PRIORITY = 1;
    /** Priority level for the hunting behavior during alarms. */
    private static final int HUNT_PRIORITY = 2;

    public StaticStalker() {
        super("Static Stalker", 'S', 50, new BasicInventory());
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        this.behaviours.put(HUNT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        // COMPLEX EFFECT: Stun Aura (PDF Page 4 mentions Paralyzed AoE)
        for (Exit exit : here.getExits()) {
            Location adj = exit.getDestination();
            if (adj.containsAnActor()) {
                Actor target = adj.getActor();
                if (target.hasAbility(Ability.WORKER)) {
                    // 20% chance to stun nearby workers (PDF Page 4 & 12 logic)
                    if (Math.random() < 0.20) {
                        target.addStatus(new ParalyzedStatus(1));
                        display.println(this + "'s Aura stuns " + target + "!");
                    }
                }
            }
        }

        return super.playTurn(actions, lastAction, map, display);
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new GalvanicStrike(STRIKE_DAMAGE, STRIKE_HIT_RATE);
    }
}
