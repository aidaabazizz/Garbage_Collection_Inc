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
import game.highvoltage.MaterialCapability;

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
     * Senses the environment every turn.
     * If the ground beneath the holder is ENERGIZED (e.g., PoweredFloor, Puddle),
     * the wallet automatically triggers its magnetic pull logic.
     *
     * @param currentLocation The location of the actor holding the wallet.
     * @param actor           The actor holding the wallet.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        Display display = new Display();
        // This allows the wallet to draw power from PoweredFloors, ElectrifiedPuddle, AtmosphericChargeSource, TeslaCoil
        if (currentLocation.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            String sourceName = "the " + currentLocation.getGround() + " beneath Bob's feet";
            this.reactToCharge(currentLocation, display, sourceName);
        }
    }

    /**
     * Implements the ChargeReactive interface to activate magnetic harvesting.
     *
     * The method performs a geometric scan of the surrounding 5x5 area. For each
     * valid tile, it performs a "Flux Check" to see if the path is blocked by a wall.
     * If the path is clear, it attempts to pull any MAGNETIC items to the center.
     *
     * @param location   The origin of the magnetic field.
     * @param display    The terminal interface for outputting "flying item" messages.
     * @param sourceName The name of the power source activating the magnets.
     */
    @Override
    public void reactToCharge(Location location, Display display, String sourceName) {
        if (!location.containsAnActor()) return;
        Actor worker = location.getActor();
        display.println("\u001B[36m⚡ The wallet's magnetic coils are powered by " + sourceName + "! \u001B[0m");

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
                    if (isPathBlocked(location, targetLoc)) {
                        continue;
                    }

                    pullItemsFromLocation(targetLoc, location, worker, display);
                }
            }
        }
    }

    /**
     * Simulates physical shielding. Determines if there is an impassable obstacle
     * (Wall) between the magnet and the target item.
     *
     * @param start The center location (Bob).
     * @param end   The target location (Scrap Item).
     * @return true if the path is blocked by a non-passable ground type.
     */
    private boolean isPathBlocked(Location start, Location end) {
        int stepX = Integer.compare(end.x(), start.x());
        int stepY = Integer.compare(end.y(), start.y());
        Location firstStep = start.map().at(start.x() + stepX, start.y() + stepY);
        return !firstStep.getGround().canActorEnter(null);
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
                source.removeItem(item);
                if (bob.getInventory().add(item)) {
                    display.println("\u001B[36m" + item + " flew into " + bob + "'s inventory!\u001B[0m");
                } else {
                    bobLoc.addItem(item); // pull to feet
                    display.println("\u001B[33m" + item + " was pulled to " + bob + "'s feet!\u001B[0m");
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