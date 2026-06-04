package game.highvoltage;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A stateless utility class for coordinating common galvanic impact operations.
 *
 * Strictly follows SOLID principles by centralizing the "Standard Impact" of
 * electrical surges. This ensures consistent behavior across all Emitters
 * (Lightning, Tesla Coils, and Portable Batteries) while decoupling the source
 * of the energy from the objects receiving it.
 *
 * @author Jewell Gomes
 */
public class ChargeUtils {
    /** The standard duration (in turns) for the Shocked status applied by a surge. */
    private static final int SHOCK_DURATION = 2;

    /**
     * Private constructor to prevent instantiation.
     * As a stateless utility class, it should only be accessed via its static methods.
     */
    private ChargeUtils() {}

    /**
     * Coordinates the high-voltage effects on a specific map tile.
     *
     * This method manages a multi-stage interaction chain:
     * 1. Recursion Guard: Uses the {@link ChargeContext} memory to ensure a single
     *    electrical wave only "zaps" a specific coordinate once, preventing
     *    duplicate damage and infinite loops.
     * 2. Actor Impact: Deals damage and optionally applies {@link ShockedStatus}.
     * 3. Biological Metamorphosis: Triggers evolution in {@link ChargeReactive}
     *    actors (e.g., dormant creatures).
     * 4. Inventory Induction: Triggers magnetic items (e.g., Wallet) held by the actor.
     * 5. Environmental Induction: Triggers reactive items lying on the floor.
     *
     * @param target      The location being energized.
     * @param charge      The charge context containing damage values and propagation memory.
     * @param applyStatus true if the strike should apply a Shocked status to actors.
     */
    public static void zapTile(Location target, ChargeContext charge, boolean applyStatus) {
        // If this energy wave already zapped this specific tile, stop.
        // This ensures Bob only takes ZAP_DAMAGE once per lightning strike.
        // recursion guard to ensure bob only takes damage once per lightning wave.
        if (!charge.visit(target)) {
            return;
        }

        // actor interaction
        if (target.containsAnActor()) {
            Actor victim = target.getActor();

            // core Combat Effects (Requirement consistency)
            victim.hurt(charge.getDamage());
            // only apply status if it's a high-voltage strike (Tesla Coil/Lightning)
            if (applyStatus) {
                victim.addStatus(new ShockedStatus(SHOCK_DURATION));
            }
            charge.getDisplay().println(String.format("\u001B[35m %s is caught in %s!\u001B[0m",
                    victim, charge.getSourceName()));

            // metamorphosis: Egg -> Stalker
            victim.asCapability(ChargeReactive.class)
                    .ifPresent(reactiveActor -> reactiveGroundAction(target, reactiveActor, charge));

            // inventory: Magnetize Wallet
            victim.getInventory().getItemsAs(ChargeReactive.class)
                    .forEach(item -> item.reactToCharge(target, charge));
        }

        // triggers reactive items lying on the ground (a dropped Wallet)
        target.getItemsAs(ChargeReactive.class)
                .forEach(item -> item.reactToCharge(target, charge));
    }

    /**
     * Safely executes a reaction for Ground objects.
     * Centralizes the logic to ensure Emitters (like Tesla Coils) can trigger
     * terrain changes (like morphing Puddles) without needing explicit null checks.
     *
     * @param location The location whose ground is being energized.
     * @param charge   The context of the galvanic charge.
     */
    public static void triggerGroundReaction(Location location, ChargeContext charge) {
        ChargeReactive ground = location.getGroundAs(ChargeReactive.class);
        if (ground != null) {
            ground.reactToCharge(location, charge);
        }
    }

    /**
     * Internal helper to execute a biological metamorphosis for actors.
     * Keeps the primary {@code zapTile} method clean and readable.
     *
     * @param loc    The location of the reactive entity.
     * @param r      The reactive entity (Actor).
     * @param charge The charge context.
     */
    private static void reactiveGroundAction(Location loc, ChargeReactive r, ChargeContext charge) {
        r.reactToCharge(loc, charge);
    }
}
