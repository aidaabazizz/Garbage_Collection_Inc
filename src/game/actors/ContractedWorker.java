package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.RageStrikeAction;
import game.actions.DisorientedMoveAction;
import game.capabilities.*;
import game.enums.Ability;
import game.managers.AlarmManager;
import game.managers.Spawner;
import game.sanctuary.DamageInterceptor;
import game.utils.SpatialSearch;
import game.weapons.WorkerFists;

import java.util.HashSet;
import java.util.Set;

/**
 * The primary player-controlled actor representing a contracted worker.
 * KISS: All complex movement math (Inversion) has been removed.
 * Spatiotemporal warping is handled by the BlackHoleStatus.
 */
public class ContractedWorker extends Actor implements Infectable, Freezable, Disorientable {

    private int spawnCounter = 0;
    private static final int SPAWN_THRESHOLD = 5;
    private final Spawner spawner;

    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory, Spawner spawner) {
        super(name, displayChar, hitPoints, inventory);
        this.spawner = spawner;
        this.enableAbility(Ability.WORKER);
        this.setIntrinsicWeapon(new WorkerFists());
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        display.endLine();
        // Reset protection flag every turn (KISS Reset Pattern)
        this.disableAbility(DamageInterceptor.PROTECTED);

        // 1. Basic State Checks
        if (AlarmManager.getInstance().isActive()) {
            display.println("\u001B[31m!!! RED ALERT: FACILITY LOCKED DOWN !!!\u001B[0m");
        }

        if (!this.isConscious()) return new DoNothingAction();

        if (this.hasStatus(FrozenStatus.class)) {
            display.println("\u001B[36m" + this + " is frozen solid!\u001B[0m");
            return new DoNothingAction();
        }

        // 2. Status Display (Shows Black Hole Warp turns, etc.)
        for (Status status : this.statuses()) {
            display.println(this.name + " is affected by: " + status.toString());
        }

        // 3. REQ4: KILLER MODE (Using SpatialSearch Utility)
        if (this.hasStatus(KillerInstinctStatus.class)) {
            injectRageStrikeActions(actions, map);
        }

        // 4. REQ5: BLIZZARD DISORIENTATION (Sneaky Movement from A2)
        ActionList processedActions = actions;
        if (this.hasStatus(BlizzardDisorientationStatus.class)) {
            processedActions = processDisorientedActions(actions);
        }

        // 5. Final Menu Presentation
        // Bob will land here AFTER the Black Hole warped him during the map tick!
        return new Menu(processedActions).showMenu(this, display);
    }

    /**
     * REQ4 Helper: Scans a 3-tile radius and injects RageStrikeAction.
     */
    private void injectRageStrikeActions(ActionList actions, GameMap map) {
        Location here = map.locationOf(this);
        for (Actor target : SpatialSearch.getActorsWithinDistance(here, 3)) {
            if (target != this) {
                actions.add(new RageStrikeAction(target, "range", getIntrinsicWeapon()));
            }
        }
    }

    /**
     * REQ4: DAMAGE MITIGATION (Sanctuary Logic)
     */
    @Override
    public void hurt(int points) {
        if (this.hasAbility(DamageInterceptor.PROTECTED)) {
            points = Math.max(1, points / 2);
        }
        super.hurt(points);
    }

    /**
     * REQ5 Helper: Blizzard disorientation wrapping (A2 Logic).
     */
    private ActionList processDisorientedActions(ActionList actions) {
        ActionList wrapped = new ActionList();
        Set<String> addedDirections = new HashSet<>();
        String[] allDirections = {"North", "South", "East", "West", "North-East", "South-East", "South-West", "North-West"};
        String[] hotKeys = {"8", "2", "6", "4", "9", "3", "1", "7"};

        for (Action action : actions.getUnmodifiableActionList()) {
            String description = action.menuDescription(this);
            boolean isMove = false;
            for (int i = 0; i < allDirections.length; i++) {
                if (description.contains(allDirections[i]) && !addedDirections.contains(allDirections[i])) {
                    addedDirections.add(allDirections[i]);
                    wrapped.add(new DisorientedMoveAction(allDirections[i], hotKeys[i]));
                    isMove = true;
                    break;
                }
            }
            if (!isMove) wrapped.add(action);
        }
        return wrapped;
    }

    @Override public void reactToInfection(Location loc) { this.addStatus(new InfectionStatus()); }
    @Override public void updateInfection(Location loc) {
        if (++spawnCounter >= SPAWN_THRESHOLD) {
            spawnCounter = 0;
            this.spawner.spawnParasite(loc);
        }
    }
    @Override public void freeze(int duration) { this.addStatus(new FrozenStatus(duration)); }
    @Override public void disorient(int duration) { this.addStatus(new BlizzardDisorientationStatus(duration)); }
}