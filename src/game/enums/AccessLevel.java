package game.enums;

/**
 * Represents the security clearance level of an access card or door.
 *
 * @author Suchir
 * @version 1.0
 */
public enum AccessLevel {
    LEVEL_ONE(1),
    LEVEL_TWO(2),
    LEVEL_THREE(3);

    private final int rank;

    /**
     * Constructor for AccessLevel.
     *
     * @param rank the numeric ranking of the access level
     */
    AccessLevel(int rank) {
        this.rank = rank;
    }

    /**
     * Checks whether this access level can open a required access level.
     *
     * @param requiredLevel the required access level
     * @return true if this level is high enough
     */
    public boolean canOpen(AccessLevel requiredLevel) {
        return this.rank >= requiredLevel.rank;
    }

    /**
     * Gets the displayable level number.
     *
     * @return level number
     */
    public int getRank() {
        return rank;
    }
}