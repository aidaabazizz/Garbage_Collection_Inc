package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.highvoltage.ChargeContext;
import game.highvoltage.ChargeReactive;
import game.highvoltage.GalvanicCharge;
import game.enums.MaterialCapability;
import game.inventory.BasicInventory;

import java.util.ArrayList;

/**
 * A specialized NonPlayerCharacter that represents a bioelectrical "egg" or dormant entity.
 * This class is a core component of Requirement 3 (High-Voltage Galvanic System).
 *
 * The DormantStaticCreature remains completely inert and harmless until it is exposed to
 * a high-voltage galvanic charge. It acts as a "Resonator" in the galvanic ecosystem,
 * capable of detecting ENERGIZED terrain or receiving a direct surge from a ChargeSource.
 *
 * Complexity Proof (Rule 2):
 * This class demonstrates "Dynamic Evolution" — a structural map change where one
 * actor is permanently replaced by a more complex hostile predator (StaticStalker)
 * upon triggering an environmental condition.
 *
 * ADDITIONAL COMPLEXITY (Magnetic Anomaly):
 * 1. AoE magnetic repulsion: Each charge increases anomaly radius; at max radius (3),
 *    repels all magnetic items within Manhattan distance ≤ 3.
 * 2. Item teleportation: Pushes magnetic items 2 tiles further away from the egg.
 * 3. State tracking: anomalyRadius persists across turns and charges.
 *
 * @author Jewell Gomes
 */
public class DormantStaticCreature extends NonPlayerCharacter implements ChargeReactive {
    /** The initial health points of the dormant entity. */
    private static final int INITIAL_HEALTH = 10;
    /**
     * The base damage value used when the creature creates a local charge context
     * upon absorbing energy from the ground.
     */
    private static final int DAMAGE = 1;
    /** Current radius of the magnetic anomaly (0-3). Increases with each charge. */
    private int anomalyRadius = 0;
    /** Maximum radius before pulse triggers. */
    private static final int MAX_ANOMALY_RADIUS = 3;
    /** How many tiles away items get pushed. */
    private static final int PUSH_DISTANCE = 2;

    /**
     * Constructor for the DormantStaticCreature.
     * Initializes the entity with the 'O' symbol, representing a bioelectrical pod or egg.
     */
    public DormantStaticCreature() {
        super("Dormant Static Creature", 'O', INITIAL_HEALTH, new BasicInventory());
        this.enableAbility(MaterialCapability.DORMANT_EGG);  // ADDED for identification
    }

    /**
     * Processes the dormant entity's turn.
     *
     * Instead of moving or attacking, this entity performs an environmental check on its
     * current location. If the ground possesses the ENERGIZED capability, the creature
     * absorbs the charge and triggers its metamorphosis logic.
     *
     * @param actions    A collection of available actions (ignored by this actor).
     * @param lastAction The action performed in the previous turn.
     * @param map        The current game map.
     * @param display    The terminal interface for logging charge absorption.
     * @return A DoNothingAction, as the entity remains stationary until it evolves.
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location here = map.locationOf(this);

        // actor-to-Ground interaction
        if (here.getGround().hasAbility(MaterialCapability.ENERGIZED)) {
            display.println(this + " absorbs charge from the ground!");
            String sourceName = "the " + here.getGround() + " beneath its feet";
            ChargeContext passiveWave = new GalvanicCharge(sourceName, display, DAMAGE);
            this.reactToCharge(here, passiveWave);
        }
        return new DoNothingAction();
    }

    /**
     * Implements the ChargeReactive interface to handle high-voltage metamorphosis.
     *
     * When a charge is received (either from the ground or a direct pulse), this method:
     * 1. (NEW) Increases magnetic anomaly radius based on charge damage.
     * 2. (NEW) Triggers magnetic repulsion pulse if radius reaches maximum.
     * 3. (ORIGINAL) Logs evolution and spawns a StaticStalker.
     *
     * @param location The location where the charge was received.
     * @param charge   The GalvanicCharge object containing source details and display logic.
     */
    @Override
    public void reactToCharge(Location location, ChargeContext charge) {
        Display display = charge.getDisplay();

        int chargeDamage = charge.getDamage();
        anomalyRadius = Math.min(MAX_ANOMALY_RADIUS, anomalyRadius + chargeDamage);

        if (anomalyRadius > 0) {
            display.println("\u001B[35m" + this + " magnetic anomaly: " + anomalyRadius + "/" + MAX_ANOMALY_RADIUS +
                    " (from " + charge.getSourceName() + ")\u001B[0m");

            // check if we've reached critical mass - trigger magnetic pulse
            if (anomalyRadius >= MAX_ANOMALY_RADIUS) {
                executeMagneticPulse(location, display);
                anomalyRadius = 0;  // Reset after pulse

                // hatch only after pulse
                display.println("\u001B[33mThe " + this + " is stimulated by " + charge.getSourceName() + " and shatters!\u001B[0m");
                display.println("\u001B[33mA Static Stalker has been born!\u001B[0m");
                location.map().removeActor(this);
                try {
                    location.map().addActor(new StaticStalker(), location);
                } catch (Exception e) {
                    display.println("Evolution failed: Tile at " + location + " is blocked.");
                }
            }
            // If not at max radius, egg stays alive and continues accumulating
        }
    }

    /**
     * Executes the magnetic repulsion pulse.
     * Scans a diamond-shaped area (Manhattan distance ≤ MAX_ANOMALY_RADIUS)
     * and pushes all magnetic items away from the egg.
     */
    private void executeMagneticPulse(Location center, Display display) {
        display.println("\u001B[35m" + this + " releases a magnetic pulse! Repelling items in radius " + MAX_ANOMALY_RADIUS + "!\u001B[0m");

        int itemsRepelled = 0;

        // Scan all tiles within Manhattan distance <= MAX_ANOMALY_RADIUS
        for (int x = -MAX_ANOMALY_RADIUS; x <= MAX_ANOMALY_RADIUS; x++) {
            for (int y = -MAX_ANOMALY_RADIUS; y <= MAX_ANOMALY_RADIUS; y++) {

                // Manhattan distance check (diamond shape, not square)
                if (Math.abs(x) + Math.abs(y) <= MAX_ANOMALY_RADIUS) {

                    int targetX = center.x() + x;
                    int targetY = center.y() + y;

                    // Boundary check
                    if (!center.map().getXRange().contains(targetX) ||
                            !center.map().getYRange().contains(targetY)) {
                        continue;
                    }

                    Location targetLoc = center.map().at(targetX, targetY);

                    // Skip the egg's own tile
                    if (targetX == center.x() && targetY == center.y()) {
                        continue;
                    }

                    // Check for magnetic items on this tile
                    for (Item item : new ArrayList<>(targetLoc.getItems())) {
                        if (item.hasAbility(MaterialCapability.MAGNETIC)) {
                            if (repelItem(targetLoc, center, item, display)) {
                                itemsRepelled++;
                            }
                        }
                    }
                }
            }
        }

        if (itemsRepelled > 0) {
            display.println("\u001B[35mMagnetic pulse repelled " + itemsRepelled + " items!\u001B[0m");
        } else {
            display.println("\u001B[35mNo magnetic items found in pulse radius.\u001B[0m");
        }
    }

    /**
     * Repels a single item away from the egg.
     * Calculates push direction based on item's position relative to egg.
     */
    private boolean repelItem(Location itemLoc, Location eggLoc, Item item, Display display) {
        // Calculate direction vector from egg to item
        int deltaX = itemLoc.x() - eggLoc.x();
        int deltaY = itemLoc.y() - eggLoc.y();

        // Calculate push destination (continue in same direction)
        int pushX = itemLoc.x();
        int pushY = itemLoc.y();

        if (deltaX != 0) {
            pushX += (deltaX > 0 ? PUSH_DISTANCE : -PUSH_DISTANCE);
        }
        if (deltaY != 0) {
            pushY += (deltaY > 0 ? PUSH_DISTANCE : -PUSH_DISTANCE);
        }

        // Boundary check for push destination
        if (!itemLoc.map().getXRange().contains(pushX) ||
                !itemLoc.map().getYRange().contains(pushY)) {
            display.println("\u001B[33m" + item + " cannot be repelled further (map edge)!\u001B[0m");
            return false;
        }

        Location pushLoc = itemLoc.map().at(pushX, pushY);

        // Remove from current location and add to new location
        itemLoc.removeItem(item);
        pushLoc.addItem(item);

        display.println("\u001B[35m" + item + " is repelled from (" + itemLoc.x() + "," + itemLoc.y() +
                ") to (" + pushX + "," + pushY + ")!\u001B[0m");

        return true;
    }
}