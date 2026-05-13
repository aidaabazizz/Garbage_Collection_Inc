package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;

/**
 * Utility class for wrapping movement actions with disoriented versions.
 * Uses functional approach - NO switch!
 *
 * @author Aida
 */
public class MovementActionWrapper {

    /**
     * Wraps movement actions in an ActionList with disoriented versions.
     * Uses policy-based replacement - NO switch!
     */
    public static ActionList wrapMovementActions(ActionList originalActions, Actor actor,
                                                 java.util.function.Function<String, String> hotKeyExtractor) {
        ActionList newActions = new ActionList();

        for (Action action : originalActions.getUnmodifiableActionList()) {
            String description = action.menuDescription(actor);

            // Check if this is a movement action by looking for direction keywords
            boolean isMovementAction = false;
            String direction = null;

            // Use array of directions to check - NO switch!
            String[] directions = {"North", "South", "East", "West"};
            for (String dir : directions) {
                if (description.contains(" moves " + dir)) {
                    isMovementAction = true;
                    direction = dir;
                    break;
                }
            }

            if (isMovementAction && direction != null) {
                String hotKey = hotKeyExtractor.apply(direction);
                newActions.add(new DisorientedMoveAction(direction, hotKey));
            } else {
                newActions.add(action);
            }
        }

        return newActions;
    }
}