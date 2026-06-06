package game.capabilities;

/**
 * A contract for items that generate background notifications during the actor's turn.
 * This interface allows inventory items to communicate spontaneous events,
 * such as lantern fuel leaks, directly to the user interface (REQ2).
 *
 * @author Jewell Gomes
 */
public interface UpdateNotifier {
    /**
     * Retrieves the most recent background notification message.
     * @return A descriptive message if an event occurred, null otherwise.
     */
    String updateMessage();
}
