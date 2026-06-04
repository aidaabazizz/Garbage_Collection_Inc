package edu.monash.fit2099.engine.positions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Printable;
import edu.monash.fit2099.engine.items.Item;
import java.util.*;

/**
 * Class representing a location in the game map. This includes keeping track of
 * exits, character representation, terrain type, and other game data.
 *
 * @author Riordan Alfredo
 */
public class Location implements Printable {

    private final GameMap map;
    private final int x;
    private final int y;

    private final List<Item> items = new ArrayList<>();
    private Ground ground;
    private final List<Exit> exits = new ArrayList<>();

    /**
     * Constructor.
     * <p>
     * Locations know which map they are part of, and where.
     *
     * @param map the map that contains this location
     * @param x x coordinate of this location within the map
     * @param y y coordinate of this location within the map
     */
    public Location(GameMap map, int x, int y) {
        this.map = map;
        this.x = x;
        this.y = y;
    }

    /**
     * Accessor for the Location's map.
     *
     * @return the map that contains this Location
     */
    public GameMap map() {
        return map;
    }

    /**
     * Accessor for the x coordinate.
     *
     * @return the x coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Accessor for the y coordinate.
     *
     * @return the y coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Returns a list of items at this location.
     * <p>
     * This list uses Collections.unmodifiableList() to prevent privacy leaks.
     *
     * @return an unmodifiable List of items at this location
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns an unmodifiable list of all capabilities of the specified type
     * provided by items at this location.
     * <p>
     * For each item, attempts to retrieve the specified capability using
     * {@code asCapability}. If the capability is present, it is included in
     * the returned list. The returned list cannot be modified.
     *
     * @param <T> the type of capability to retrieve
     * @param capability the class object representing the capability type
     * @return an unmodifiable list containing all capabilities of the specified type
     *         found among the items at this location; the list is empty if no items
     *         provide the capability
     */
    public <T> List<T> getItemsAs(Class<T> capability) {
        List<T> result = new ArrayList<>();
        for (Item item : items) {
            if(item != null) {
                item.asCapability(capability).ifPresent(result::add);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Add an item to this location.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        Objects.requireNonNull(item);
        items.add(item);
    }

    /**
     * Remove an item from this location, if it is here.
     *
     * @param item the item to remove
     */
    public void removeItem(Item item) {
        Objects.requireNonNull(item);
        items.remove(item);
    }

    /**
     * Accessor for the ground at this location.
     *
     * @return the ground at this location
     */
    public Ground getGround() {
        return ground;
    }


    /**
     * Returns the specified capability provided by the ground at this location, if available.
     * Attempts to retrieve the capability from the ground using {@code asCapability}.
     * If the ground does not support the requested capability, this method returns {@code null}.
     *
     * @param <T> the type of capability to retrieve
     * @param capability the class object representing the capability type
     * @return the capability provided by the ground, or {@code null} if not available
     */
    public <T> T getGroundAs(Class<T> capability) {
        if(ground == null) {
            return null;
        }
        return ground.asCapability(capability).orElse(null);
    }

    /**
     * Set the Ground type at the given Location
     *
     * @param ground Ground type to set
     */
    public void setGround(Ground ground) {
        this.ground = ground;
    }

    /**
     * Called once per turn, so that Locations can experience the passage time.
     * If that's important to them. All items on this ground will also
     * experience time.
     */
    public void tick() {
        ground.tick(this);
        ground.tickStatuses(this);
        for (Item item : new ArrayList<>(items)) {
            item.tick(this);
            item.tickStatuses(this);
        }
    }

    /**
     * Accessor to determine whether there is an Actor at this location.
     *
     * @return true if and only if there is an Actor at this location.
     */
    public boolean containsAnActor() {
        return map.isAnActorAt(this);
    }

    /**
     * Accessor to retrieve the Actor at this location.
     *
     * @return the Actor at this location, if there is one
     */
    public Actor getActor() {
        return map.getActorAt(this);
    }


    /**
     * Returns the actor at this location as the specified type, if available.
     * If the actor does not support the requested capability, returns null.
     *
     * @param <T> the type of capability to retrieve
     * @param type the class object representing the capability type
     * @return the actor as the specified type, or null if not available
     */
    public final <T> T getActorAs(Class<T> type) {
        Actor actor = map.getActorAt(this);
        if (actor == null) {
            return null;
        }
        return actor.asCapability(type).orElse(null);
    }

    /**
     * Add an Actor to the Location. This is really only here for consistency
     * for the Location API.
     *
     * @param actor the Actor to add
     */
    public void addActor(Actor actor) throws GameEngineException {
        Objects.requireNonNull(actor);
        map.addActor(actor, this);
    }

    /**
     * Returns a MoveActorAction that will move actor to location if the terrain
     * type allows.
     *
     * @param actor the Actor to move
     * @param direction the direction of the destination from actor
     * @param hotKey the character that will trigger the movement command
     * @return a MoveActorAction that allows actor to move to location if
     * allowed; otherwise null
     */
    public MoveActorAction getMoveAction(Actor actor, String direction, String hotKey) {
        if (canActorEnter(actor)) {
            return new MoveActorAction(this, direction, hotKey);
        }

        return null;
    }

    /**
     * Returns true if an Actor can enter this location.
     * <p>
     * Actors can enter a location if the terrain is passable and there isn't an
     * Actor there already. Game rule. One actor per location.
     *
     * @param actor the Actor who might be moving
     * @return true if the Actor can enter this location
     */
    public boolean canActorEnter(Actor actor) {
        return !map.isAnActorAt(this) && ground.canActorEnter(actor);
    }

    /**
     * Returns the character to display for this location.
     * <p>
     * If there is an Actor here, they are drawn. If there is no Actor, then any
     * items present are drawn. If there is no Actor and no item, then the
     * ground symbol is drawn.
     *
     * @return the symbol to draw for this location
     */
    @Override
    public char getDisplayChar() {
        Printable thing;

        if (this.containsAnActor()) {
            thing = this.getActor();
        } else if (!items.isEmpty()) {
            thing = items.get(items.size() - 1);
        } else {
            thing = ground;
        }

        return thing.getDisplayChar();
    }

    /**
     * Computes a hash for the current Location.
     *
     * @return the hash
     */
    @Override
    public int hashCode() {
        return map.hashCode() ^ y() << 16 ^ x();
    }

    /**
     * Returns an unmodifiable list of exits.
     *
     * @return an unmodifiable list of exits
     */
    public List<Exit> getExits() {
        return Collections.unmodifiableList(exits);
    }

    /**
     * Returns a list of locations within the specified radius of this location.
     * The list includes all locations in the square area around this location,
     * excluding the current location itself. The radius is measured in map units.
     *
     * @param radius the distance from this location to search for nearby locations
     * @return an unmodifiable list of locations within the given radius, not including this location
     */
    public List<Location> getNearbyLocations(int radius){
        List<Location> result = new ArrayList<>();
        int currentX = this.x();
        int currentY = this.y();

        int minX = Math.max(this.map().getXRange().min(), currentX - radius);
        int maxX = Math.min(this.map().getXRange().max(), currentX + radius);
        int minY = Math.max(this.map().getYRange().min(), currentY - radius);
        int maxY = Math.min(this.map().getYRange().max(), currentY + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Skip the current location itself
                if (x == currentX && y == currentY) continue;
                Location loc = this.map().at(x, y);
                if (loc != null) {
                    result.add(loc);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Add an exit to this Location.
     * <p>
     * This method is used in GameMap to initialize the Location's exits.
     *
     * @param exit the exit to add
     */
    public void addExit(Exit exit) {
        exits.add(exit);
    }

    /**
     * Remove an exit from this Location.
     * <p>
     * This method is used in GameMap to initialize the Location's exits.
     *
     * @param exit the exit to remove
     */
    public void removeExit(Exit exit) {
        exits.remove(exit);
    }

    /**
     * The toString method of a Location instance prints out the x and y
     * coordinate along with the name of the map the location is on.
     *
     * @return (x, y) on map name
     */
    @Override
    public String toString() {
        return String.format("(%d, %d) on %s", x, y, map);
    }
}
