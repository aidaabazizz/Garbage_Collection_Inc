package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.*;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.UseCommandWhistleAction;
import game.capabilities.CreditHolder;
import game.capabilities.MotivationStatus;
import game.capabilities.Purchasable;
import game.capabilities.Sellable;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.sanctuary.SanctuaryTool;
import game.utils.SpatialSearch;

public class CommandWhistle extends Item implements SanctuaryTool, Purchasable {

    public CommandWhistle() {
        super("Command Whistle", 'f');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(1));
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new UseCommandWhistleAction(this));
        return actions;
    }

    @Override
    public String activateSanctuaryEffect(Actor actor, GameMap map, Location location) {
        // 1. SCAN: Use the utility to find WHO gets motivated
        Actor pulseOriginActor = SpatialSearch.findNearestWorkerWithinDistance(map, location, 5);
        if (pulseOriginActor == null) pulseOriginActor = actor;

        // 2. APPLY: Give that worker the status.
        // We pass 'location' (the user's spot) as the center of the blast.
        pulseOriginActor.addStatus(new MotivationStatus(location));

        // 3. CLEANUP
        actor.getInventory().remove(this);

        return String.format("%s blows the whistle! A pulse of leadership radiates from %s!",
                actor, pulseOriginActor);
    }

    // --- Purchasable Implementation ---
    @Override
    public int getPurchasePrice() {
        return 100; // Rare item price
    }

    @Override
    public String purchasedBy(Actor buyer, GameMap map, CreditHolder wallet) {
        wallet.deductCredits(this.getPurchasePrice());
        buyer.getInventory().add(this);
        return buyer + " bought a Heaven Token for 100 credits.";
    }

}