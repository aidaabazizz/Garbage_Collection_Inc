package game.finance;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.capabilities.CreditHolder;
import game.enums.Ability;
import game.enums.ItemStatistics;
import edu.monash.fit2099.engine.items.Item;
import game.highvoltage.ChargeReactive;
import game.highvoltage.GalvanicCharge;
import game.enums.MaterialCapability;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a worker's credit storage for company transactions.
 * The wallet is stored as a non-portable item in the worker's inventory.
 *
 * In addition to handling financial transactions, the Wallet is a core "Resonator"
 * in the High-Voltage Galvanic System (REQ3). It functions as an electromagnet,
 * activating its magnetic properties when the holder stands on ENERGIZED terrain
 * or is struck by a direct galvanic charge.
 *
 * Complexity Proof (Rule 2 + Realistic Physics):
 * 1. Directional Flux Shielding: Uses coordinate math to determine if structural
 *    walls block the magnetic pull from a specific direction.
 * 2. Area-of-Effect (AoE): Scans a 5x5 grid (Radius 2) around the holder.
 * 3. Inventory Integration: Validates the holder's weight capacity, pulling items
 *    to the holder's feet if their inventory is full.
 *
 * @author Suchir
 * @version 1.0
 */
public class Wallet extends Item implements CreditHolder, ChargeReactive {
    private static final int MAX_CREDITS = 1000;
    /** The radius of the magnetic field in tiles. */
    private static final int MAGNETIC_RADIUS = 2;
    /** The damage value associated with the wallet's induction wave (none). */
    private static final int DAMAGE = 0;
    private int credits;

    /**
     * Constructor for Wallet.
     */
    public Wallet() {
        super("Wallet", '$');
        this.credits = 0;
        this.makeNonPortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(0));
        this.enableAbility(Ability.ESSENTIAL);
        this.enableAbility(MaterialCapability.MAGNETIC);
    }

    /**
     * Adds credits to the wallet without exceeding the maximum limit.
     *
     * @param amount the amount of credits to add
     * @return the actual number of credits added
     */
    @Override
    public int addCredits(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int current = credits;
        credits = Math.min(MAX_CREDITS, credits + amount);
        return credits - current;
    }

    /**
     * Deducts credits from the wallet if enough credits are available.
     *
     * @param amount the amount of credits to deduct
     * @return true if the deduction succeeds, false otherwise
     */
    @Override
    public boolean deductCredits(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (credits < amount) {
            return false;
        }
        credits -= amount;
        return true;
    }

    /**
     * Forcefully deducts credits, stopping at zero.
     *
     * @param amount the amount of credits to deduct
     * @return the actual number of credits deducted
     */
    @Override
    public int forceDeductCredits(int amount) {
        if (amount <= 0) {
            return 0;
        }
        int before = credits;
        credits = Math.max(0, credits - amount);
        return before - credits;
    }

    /**
     * Checks whether the wallet has enough credits.
     *
     * @param amount the required amount
     * @return true if enough credits are available
     */
    @Override
    public boolean hasEnough(int amount) {
        return credits >= amount;
    }

    /**
     * Gets the current credits.
     *
     * @return current credits
     */
    @Override
    public int getCredits() {
        return credits;
    }

    /**
     * Senses the environment each turn to facilitate Actor-to-Ground synergy.
     * If the ground beneath the holder possesses the ENERGIZED capability (e.g.,
     * PoweredFloor or Puddle), the wallet draws power and triggers its magnetic pull.
     *
     * @param currentLocation The location of the actor holding the wallet.
     * @param actor           The actor carrying the wallet.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        // This allows the wallet to draw power from PoweredFloors, ElectrifiedPuddle, AtmosphericChargeSource, TeslaCoil
        if (currentLocation.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            GalvanicCharge passiveWave = new GalvanicCharge("the energized ground", new Display(), DAMAGE);
            this.reactToCharge(currentLocation, passiveWave);
        }
    }

    /**
     * Implements the ChargeReactive interface to activate magnetic harvesting.
     *
     * When triggered by a charge, this method:
     * 1. Scans a 5x5 grid centered on the holder.
     * 2. Performs a "Flux Block" check for every tile in the radius to ensure
     *    walls do not obstruct the magnetic field.
     * 3. Pulls all items with the MAGNETIC capability toward the holder.
     *
     * @param location The origin location where the charge was received.
     * @param charge   The context of the galvanic charge triggering the reaction.
     */
    @Override
    public void reactToCharge(Location location, GalvanicCharge charge) {
        if (!location.containsAnActor()) return;
        Actor worker = location.getActor();
        Display display = charge.getDisplay();
        display.println("\u001B[36m The wallet's magnetic coils are powered by " + charge.getSourceName() + "! \u001B[0m");

        pullItemsFromLocation(location, location, worker, charge.getDisplay());

        // scan a 5x5 square with the Worker in the center
        for (int x = -MAGNETIC_RADIUS; x <= MAGNETIC_RADIUS; x++) {
            for (int y = -MAGNETIC_RADIUS; y <= MAGNETIC_RADIUS; y++) {
                if (x == 0 && y == 0) continue;

                int targetX = location.x() + x;
                int targetY = location.y() + y;

                // map boundary safety
                if (location.map().getXRange().contains(targetX) &&
                        location.map().getYRange().contains(targetY)) {

                    Location targetLoc = location.map().at(targetX, targetY);

                    // we check if there is a wall between Bob and the target tile
                    if (!isPathBlocked(location, targetLoc)) {
                        pullItemsFromLocation(targetLoc, location, worker, display);
                    }
                }
            }
        }
    }

    /**
     * Simulates physical flux shielding using a ray-casting approximation.
     *
     * This method determines if an impassable obstacle (e.g., a Wall) exists
     * along the direct vector between the magnetic source and the target item.
     *
     * Visual/Mechanical Impact:
     * 1. Magnetic Shadowing: If a wall is detected at distance 1, it casts a
     *    shadow that prevents the pull of items at distance 2.
     * 2. Structural Synergy: Ensures that map layout directly limits the
     *    effectiveness of electrical harvesting.
     *
     * @param start The origin of the magnetic pull (the Actor).
     * @param end   The target location containing potential scrap.
     * @return true if an impassable tile blocks the path; false otherwise.
     */
    private boolean isPathBlocked(Location start, Location end) {
        int currX = start.x();
        int currY = start.y();
        int targetX = end.x();
        int targetY = end.y();

        // move step by step from Bob toward the item
        while (currX != targetX || currY != targetY) {
            currX += Integer.compare(targetX, currX);
            currY += Integer.compare(targetY, currY);

            // if we reached the item, we are done
            if (currX == targetX && currY == targetY) break;

            // if any tile in the path is a Wall, the magnetic flux is blocked
            if (!start.map().at(currX, currY).getGround().canActorEnter(null)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Manages the physical movement of pulled items.
     * If Bob has space, items are added to his inventory. If Bob is over-encumbered,
     * the kinetic energy pulls the items to his current tile instead.
     *
     * @param source  The location of the scrap item.
     * @param bobLoc  The destination location.
     * @param bob     The actor receiving the items.
     * @param display The terminal interface.
     */
    private void pullItemsFromLocation(Location source, Location bobLoc, Actor bob, Display display) {
        List<Item> itemsOnTile = new ArrayList<>(source.getItems());
        for (Item item : itemsOnTile) {
            if (item.hasAbility(MaterialCapability.MAGNETIC)) {
                // determine if the item was locked by a barrier
                String actionVerb = item.hasAbility(MaterialCapability.MAGNETICALLY_LOCKED)
                        ? "was RIPPED from the induction field"
                        : "flew";

                if (bob.getInventory().add(item)) {
                    source.removeItem(item);
                    display.println("\u001B[36m" + item + " " + actionVerb + " into " + bob + "'s inventory!\u001B[0m");
                } else if (source != bobLoc) {
                    // Only move to feet if the item isn't already at Bob's feet
                    source.removeItem(item);
                    bobLoc.addItem(item);
                    display.println("\u001B[33m" + item + " " + actionVerb + " to " + bob + "'s feet!\u001B[0m");
                }
            }
        }
    }

    /**
     * Returns the wallet's display text.
     *
     * @return wallet display text
     */
    @Override
    public String toString() {
        return "Wallet (" + credits + "/" + MAX_CREDITS + " credits)";
    }
}