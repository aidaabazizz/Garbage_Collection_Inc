package edu.monash.fit2099.engine.items;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.displays.Printable;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base class representing a physical object in the game world.
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public abstract class Item extends GameEntity implements Printable {

	private final String name;
	private final char displayChar;

	/***
	 * Constructor.
	 *
	 * @param name the name of this Item
	 * @param displayChar the character to use to represent this item if it is on the ground
	 */
	public Item(String name, char displayChar) {
		this.name = name;
		this.displayChar = displayChar;
	}

	/**
	 * Inform a carried Item of the passage of time.
	 *
	 * This method is called once per turn, if the Item is being carried.
	 * @param currentLocation The location of the actor carrying this Item.
	 * @param actor The actor carrying this Item.
	 */
	public void tick(Location currentLocation, Actor actor) {
	}

	/**
	 * Inform an Item on the ground of the passage of time.
	 * This method is called once per turn, if the item rests upon the ground.
	 * @param currentLocation The location of the ground on which we lie.
	 */
	public void tick(Location currentLocation) {
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public char getDisplayChar() {
		return displayChar;
	}


	/**
	 * Create and return an action to pick this Item up.
	 * If this Item is not portable, returns null.
	 *
	 * @return a new PickUpItemAction if this Item is portable, null otherwise.
	 */
	public PickUpAction getPickUpAction(Actor actor) {
		if(this.hasAbility(ItemAbility.PORTABLE))
			return new PickUpAction(this);
		return null;
	}

	/**
	 * Create and return an action to drop this Item.
	 * If this Item is not portable, returns null.
	 * @return a new DropItemAction if this Item is portable, null otherwise.
	 */
	public DropAction getDropAction(Actor actor) {
		if(this.hasAbility(ItemAbility.PORTABLE))
			return new DropAction(this);
		return null;
	}

	/**
	 * Make the item portable, allowing the item to be picked up or dropped by actor
	 */
	public void makePortable() {
		if (!this.hasAbility(ItemAbility.PORTABLE)) {
			this.enableAbility(ItemAbility.PORTABLE);
		}
	}

	/**
	 * Make the item non-portable, preventing the item from being picked up or dropped by actor
	 */
	public void makeNonPortable() {
		if (this.hasAbility(ItemAbility.PORTABLE)) {
			this.disableAbility(ItemAbility.PORTABLE);
		}
	}

	/**
	 * List of allowable actions that can be performed on the item when it is on the ground
	 * Example #1: a trap can return an action to disarm the trap.
	 * 
	 * @param location the location of the ground on which the item lies
	 * @return an unmodifiable list of Actions
	 */
	public ActionList allowableActions(Location location) {
		return new ActionList();
	}

	/**
	 * List of allowable actions that the item can perform to its owner
	 * or to the current map while being carried by an actor
	 * Example #1: a healing item can have a special skill that can increase the current actor's hitpoints.
	 *
	 * @param owner the actor that owns the item
	 * @param map the map where the actor is performing the action on
	 * @return an unmodifiable list of Actions
	 */
	public ActionList allowableActions(Actor owner, GameMap map) {
		return new ActionList();
	}


	/**
	 * List of allowable actions that the item allows its owner do to other actor.
	 * Example #1: a weapon can return an attacking action to the other actor.
	 * Example #2: if the weapon has a special ability, it can return an action to use the special ability.
	 * Example #3: a food can return an action to feed the other actor.
	 * 
	 * @param otherActor the other actor
	 * @param location the location of the other actor
	 * @return an unmodifiable list of Actions
	 */
	public ActionList allowableActions(Actor otherActor, Location location){
		return new ActionList();
	}
}
