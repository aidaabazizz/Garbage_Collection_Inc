package edu.monash.fit2099.demo.forest;

import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;

public class BasicInventory extends Inventory {
    @Override
    public boolean add(Item item) {
        items.add(item);
        return true;
    }

    @Override
    public boolean remove(Item item) {
        items.remove(item);
        return true;
    }
}
