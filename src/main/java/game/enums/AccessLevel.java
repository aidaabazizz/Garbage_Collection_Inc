package game.enums;

/**
 * Represents the security clearance level of an access card or door.
 * <p>
 * Each access level has a numeric rank. Higher ranked access levels can open
 * doors that require the same or lower clearance level. The enum also stores
 * the fixed card details such as card name, display character, purchase price,
 * and weight.
 * </p>
 *
 * @author Suchir
 * @version 1.0
 */
public enum AccessLevel {
    LEVEL_ONE(1, "Access Card (Level 1)", '▤', 50, 1),
    LEVEL_TWO(2, "Access Card (Level 2)", 'α', 100, 2),
    LEVEL_THREE(3, "Access Card (Level 3)", '◐', 200, 3);

    private final int rank;
    private final String cardName;
    private final char displayChar;
    private final int purchasePrice;
    private final int weight;

    /**
     * Constructor for AccessLevel.
     *
     * @param rank the numeric ranking of the access level
     * @param cardName the display name of the access card
     * @param displayChar the character used to represent the access card
     * @param purchasePrice the purchase price of the access card
     * @param weight the weight of the access card
     */
    AccessLevel(int rank, String cardName, char displayChar, int purchasePrice, int weight) {
        this.rank = rank;
        this.cardName = cardName;
        this.displayChar = displayChar;
        this.purchasePrice = purchasePrice;
        this.weight = weight;
    }

    /**
     * Checks whether this access level can open a door requiring the given access level.
     * <p>
     * This supports the requirement that higher clearance cards can open doors with
     * lower clearance requirements.
     * </p>
     *
     * @param requiredLevel the access level required by the door
     * @return true if this access level is high enough to open the door
     */
    public boolean canOpen(AccessLevel requiredLevel) {
        return this.rank >= requiredLevel.rank;
    }

    /**
     * Gets the display name of the access card.
     *
     * @return the access card name
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Gets the display character of the access card.
     *
     * @return the display character
     */
    public char getDisplayChar() {
        return displayChar;
    }

    /**
     * Gets the purchase price of the access card.
     *
     * @return the purchase price in credits
     */
    public int getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Gets the weight of the access card.
     *
     * @return the access card weight
     */
    public int getWeight() {
        return weight;
    }
}