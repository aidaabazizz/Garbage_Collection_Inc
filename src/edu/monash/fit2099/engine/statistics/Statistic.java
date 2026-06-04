package edu.monash.fit2099.engine.statistics;

/**
 * An interface that represents a attribute of an actor.
 * @author Adrian Kristanto
 * @author Riordan Alfredo
 */
public interface Statistic<T> {
    /**
     * Overwrites the current points of the attribute with the given points.
     * @param points the points to overwrite the current points of the attribute
     */
    void update(T points);

    /**
     * Increases the current points of the attribute by the given points.
     * This will add to the existing points of an attribute without overwriting the points.
     * @param points the points to increase the current points of the attribute
     */
    void increase(T points);

    /**
     * Decreases the current points of the attribute by the given points.
     * This will subtract from the existing points of an attribute without overwriting the points.
     * @param points the points to decrease the current points of the attribute
     */
    void decrease(T points);

    /**
     * Getter for the current points of the attribute.
     * @return the current points of the attribute
     */
    T get();

    /**
     * Getter for the maximum points of the attribute.
     * For example, after being attacked by an enemy, the health of the current actor is less than the maximum health, e.g. (490/500).
     * The actor will start the game with the maximum health, e.g. (500/500)
     * @return the maximum points of the attribute
     */
    default T getMaximum() {
        return get();
    }

    /**
     * Overwrites the current maximum points of the attribute with the given points.
     * For example, after being attacked by an enemy, the health of the current actor is less than the maximum health, e.g. (490/500).
     * The actor will start the game with the maximum health, e.g. (500/500)
     * @param points the points to overwrite the current maximum points of the attribute
     */
    default void updateMaximum(T points) {
        update(points);
    }

    /**
     * Increases the current maximum points of the attribute by the given points.
     * This will add to the existing maximum points of an attribute without overwriting the maximum points.
     * For example, after being attacked by an enemy, the health of the current actor is less than the maximum health, e.g. (490/500).
     * The actor will start the game with the maximum health, e.g. (500/500)
     * @param points the points to increase the current maximum points of the attribute
     */
    default void increaseMaximum(T points) {
        increase(points);
    }

    /**
     * Decreases the current maximum points of the attribute by the given points.
     * This will subtract from the existing maximum points of an attribute without overwriting the maximum points.
     * For example, after being attacked by an enemy, the health of the current actor is less than the maximum health, e.g. (490/500).
     * The actor will start the game with the maximum health, e.g. (500/500)
     * @param points the points to decrease the current maximum points of the attribute
     */
    default void decreaseMaximum(T points) {
        decrease(points);
    }
}
