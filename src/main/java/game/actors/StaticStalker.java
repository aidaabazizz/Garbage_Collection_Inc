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

/**
 * A highly hostile predator born from a high-voltage metamorphosis.
 *
 * The StaticStalker is the "Living Environmental Disaster" of Requirement 3.
 * It does not just attack; it actively leaks high-voltage flux that alters the map,
 * disrupts equipment, and incapacitates workers as it moves.
 *
 * Complexity Proof (Rule 2):
 * 1. Actor-Ground Synergy: Heals itself when standing on ENERGIZED tiles.
 * 2. Structural Terrain Morphing: Automatically electrifies Puddles it walks past.
 * 3. Inventory Disruption: Magnetizes a worker's Wallet just by standing adjacent to them.
 * 4. Behavior Modification: Inflicts ParalyzedStatus via a passive proximity check (Static Aura).
 *
 * @author Jewell Gomes
 */
public class StaticStalker extends NonPlayerCharacter{
    private static final int STRIKE_DAMAGE = 5;
    private static final int STRIKE_HIT_RATE = 75;
    private static final int INITIAL_HEALTH = 50;
    private static final int WANDER_PRIORITY = 999;
    private static final int ATTACK_PRIORITY = 1;
    private static final int HUNT_PRIORITY = 2;
    private static final double STUN_CHANCE = 0.20;

    /**
     * Constructor for the StaticStalker.
     * Initializes the predator with a prioritized behavior set: Attack > Hunt > Wander.
     */
    public StaticStalker() {
        super("Static Stalker", 'S', INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(ATTACK_PRIORITY, new AttackBehaviour());
        this.behaviours.put(HUNT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

    /**
     * Processes the Static Stalker's turn by executing its "Static Aura" logic before
     * delegating to the standard behavior tree.
     *
     * Logic Sequence:
     * 1. Ground Synergy: Checks if the current tile is ENERGIZED. If true, the stalker
     *    heals 1 HP and doubles its paralysis chance (Overcharge Mode).
     * 2. Proximity Scan: Iterates through all 8 adjacent tiles to:
     *    a) Trigger Ground Reactions (Morphing Puddles into Electrified Hazards).
     *    b) Apply Status Effects (Attempting to stun nearby workers).
     *    c) Trigger Inventory Reactions (Remotely powering items like the Wallet).
     *
     * @param actions    A collection of available actions.
     * @param lastAction The action performed in the previous turn.
     * @param map        The current game map.
     * @param display    The terminal interface.
     * @return The Action determined by the prioritized behavior tree.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        boolean isOvercharged = here.getGround().hasAbility(MaterialCapability.ENERGIZED);

        if (isOvercharged) {
            display.println(this + " is overcharged by the ground energy!");
            this.heal(1); // Actor-Ground synergy
        }

        // stun chance doubles if on power
        double currentStunChance = isOvercharged ? (STUN_CHANCE * 2) : STUN_CHANCE;

        for (Exit exit : here.getExits()) {
            Location adj = exit.getDestination();
            // trigger Ground (Morph OR Refresh)
            // if it's a Puddle -> Morphs.
            // if it's ElectrifiedPuddle -> Refreshes lifespan.
            ChargeReactive groundReactive = adj.getGroundAs(ChargeReactive.class);
            if (groundReactive != null) {
                groundReactive.reactToCharge(adj, display, this.toString());
            }

            // Actor -> Actor
            if (adj.containsAnActor()) {
                Actor target = adj.getActor();
                if (target.hasAbility(Ability.WORKER) && Math.random() < currentStunChance) {
                    target.addStatus(new ParalyzedStatus(1));
                    display.println("\u001B[35m" + this + " arced a spark into " + target + "!\u001B[0m");
                }

                // Actor -> Item
                target.getInventory().getItemsAs(ChargeReactive.class)
                        .forEach(item -> item.reactToCharge(adj, display, this.toString()));
            }
        }

        return super.playTurn(actions, lastAction, map, display);
    }

    /**
     * Returns the stalker's natural weapon, a GalvanicStrike.
     * This represents high-voltage arcs being discharged from the stalker's limbs.
     *
     * @return A new GalvanicStrike instance.
     */
    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new GalvanicStrike(STRIKE_DAMAGE, STRIKE_HIT_RATE);
    }
}
