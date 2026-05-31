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
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import game.highvoltage.ParalyzedStatus;
import game.inventory.BasicInventory;
import game.weapons.GalvanicStrike;

public class StaticStalker extends NonPlayerCharacter{
    private static final int STRIKE_DAMAGE = 5;
    private static final int STRIKE_HIT_RATE = 75;
    private static final int INITIAL_HEALTH = 50;
    private static final int WANDER_PRIORITY = 999;
    private static final int ATTACK_PRIORITY = 1;
    private static final int HUNT_PRIORITY = 2;
    private static final double STUN_CHANCE = 0.20;

    public StaticStalker() {
        super("Static Stalker", 'S', INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        this.behaviours.put(HUNT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        boolean isOvercharged = here.getGround().hasAbility(MaterialCapability.ENERGIZED);

        if (isOvercharged) {
            display.println(this + " is overcharged by the ground energy!");
            this.heal(1); // HD complexity: Actor-Ground synergy
        }

        // stun chance doubles if on power
        double currentStunChance = isOvercharged ? (STUN_CHANCE * 2) : STUN_CHANCE;

        for (Exit exit : here.getExits()) {
            Location adj = exit.getDestination();
            // 1. DIP: Trigger Ground (Morph OR Refresh)
            // If it's a Puddle -> Morphs.
            // If it's ElectrifiedPuddle -> Refreshes lifespan.
            ChargeReactive groundReactive = adj.getGroundAs(ChargeReactive.class);
            if (groundReactive != null) {
                groundReactive.reactToCharge(adj, display, this.toString());
            }

            // 2. Interaction: Actor -> Actor
            if (adj.containsAnActor()) {
                Actor target = adj.getActor();
                if (target.hasAbility(Ability.WORKER) && Math.random() < currentStunChance) {
                    target.addStatus(new ParalyzedStatus(1));
                    display.println("\u001B[35m" + this + " arced a spark into " + target + "!\u001B[0m");
                }

                // 3. Interaction: Actor -> Item
                target.getInventory().getItemsAs(ChargeReactive.class)
                        .forEach(item -> item.reactToCharge(adj, display, this.toString()));
            }
        }

        return super.playTurn(actions, lastAction, map, display);
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new GalvanicStrike(STRIKE_DAMAGE, STRIKE_HIT_RATE);
    }
}
