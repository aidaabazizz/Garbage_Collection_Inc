package edu.monash.fit2099.engine.displays;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A menu GUI implementations
 * @author Riordan Alfredo
 * @author Adrian Kristanto
 */
public class Menu {
    private static final int MAXIMUM_PAGE_LENGTH = 26;
    private ActionList actions;
    private int totalPage;

    public Menu(ActionList actions) {
        this.actions = actions;
        this.totalPage = actions.size() / MAXIMUM_PAGE_LENGTH + (actions.size() % MAXIMUM_PAGE_LENGTH > 0 ? 1 : 0);
    }

    /**
     * Display a menu to the user and have them select an option.
     *
     * @param actor   the Actor representing the player
     * @param display the I/O object that will display the map
     * @return the Action selected by the user
     */
    public Action showMenu(Actor actor, Display display) {
        return this.showMenu(actor, display, 1);
    }

    /**
     * A helper method that manages the pagination state of the menu.
     * It is set to package-private since the user doesn't need to know how the pagination system is implemented.
     * This is called by the {@link UpdatePageAction} class
     *
     * @param actor   the Actor representing the player
     * @param display the I/O object that will display the map
     * @param page    the page number of the menu
     * @return the Action selected by the user
     */
    Action showMenu(Actor actor, Display display, int page) {
        ArrayList<Character> freeChars = new ArrayList<Character>();
        HashMap<Character, Action> keyToActionMap = new HashMap<Character, Action>();

        for (char j = 'a'; j <= 'z'; j++) {
            freeChars.add(j);
        }

        display.println("Page " + page + "/" + totalPage);

        if (page < totalPage) {
            char nextPageHotKey = '>';
            UpdatePageAction updatePageAction = new UpdatePageAction(this, page + 1);
            keyToActionMap.put(nextPageHotKey, updatePageAction);
            display.println(nextPageHotKey + ": " + updatePageAction.menuDescription(actor));
        }

        if (page > 1) {
            char previousPageHotKey = '<';
            UpdatePageAction updatePageAction = new UpdatePageAction(this, page - 1);
            keyToActionMap.put(previousPageHotKey, updatePageAction);
            display.println(previousPageHotKey + ": " + updatePageAction.menuDescription(actor));
        }

        List<Action> sortedActions = actions.sorted(new SortHotkeysFirst());

        // Show with the actions with hotkeys first;
        for (int k = (page - 1) * MAXIMUM_PAGE_LENGTH; k < Math.min(page * MAXIMUM_PAGE_LENGTH, sortedActions.size()); k++) {
            String hotKey = sortedActions.get(k).hotkey();
            char c;
            if (hotKey == null || hotKey.equals("")) {
                c = freeChars.get(0);
            } else {
                c = hotKey.charAt(0);
            }
            freeChars.remove(Character.valueOf(c));
            keyToActionMap.put(c, sortedActions.get(k));
            display.println(c + ": " + sortedActions.get(k).menuDescription(actor));
        }

        char key;
        do {
            key = display.readChar();
        } while (!keyToActionMap.containsKey(key));

        return keyToActionMap.get(key);
    }

    /**
     * Inner class that provides the ability to compare two Actions.
     * This allows Actions to be sorted in order of their hotkeys.
     */
    class SortHotkeysFirst implements Comparator<Action> {
        public int compare(Action a, Action b) {
            if (a.hotkey() != null && b.hotkey() == null)
                return -1;

            if (a.hotkey() == null && b.hotkey() != null)
                return 1;

            return 0;
        }
    }
}
