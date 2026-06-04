package edu.monash.fit2099.engine.statistics;

/**
 * A class that represents a basic attribute of an actor, such as health.
 * @author Adrian Kristanto
 * @author Riordan Alfredo
 */
public class BaseStatistic implements Statistic<Integer> {
    private int maximumPoints;
    private int points;

    /**
     * At the start of the game, the points of the attributes match the maximum points.
     * @param maximumPoints the maximum points of the attribute
     */
    public BaseStatistic(int maximumPoints) {
        this.maximumPoints = maximumPoints;
        this.points = maximumPoints;
    }

    /**
     * Overwrites the current points of the attribute with the given points.
     * Sets the current points to either the given points or the maximum points, whichever is lesser.
     * @param points the points to overwrite the current points of the attribute
     */
    @Override
    public void update(Integer points) {
        this.points = Math.min(points, maximumPoints);
    }

    /**
     * Increases the current points of the attribute by the given points.
     * It ensures that the new points do not exceed the maximum points by taking either the new points or the maximum points, whichever is lesser.
     * @param points the points to increase the current points of the attribute
     */
    @Override
    public void increase(Integer points) {
        this.points += points;
        this.points = Math.min(this.points, maximumPoints);
    }

    /**
     * Decreases the current points of the attribute by the given points.
     * It ensures that the new points do not fall below 0 by taking either the new points or 0, whichever is greater.
     * For example, the stamina (another example of a basic attribute) of the actor cannot be a negative value
     * @param points the points to decrease the current points of the attribute
     */
    @Override
    public void decrease(Integer points) {
        this.points -= points;
        this.points = Math.max(this.points, 0);
    }

    /**
     * Getter for the current points of the attribute.
     * @return the current points of the attribute
     */
    @Override
    public Integer get() {
        return points;
    }

    /**
     * Getter for the maximum points of the attribute.
     * @return the maximum points of the attribute
     */
    @Override
    public Integer getMaximum() {
        return maximumPoints;
    }

    /**
     * Overwrites the current maximum points of the attribute with the given points.
     * @param points the points to overwrite the current maximum points of the attribute
     */
    @Override
    public void updateMaximum(Integer points) {
        this.maximumPoints = points;
        this.points = maximumPoints;
    }

    /**
     * Increases the current maximum points of the attribute by the given points.
     * This will add to the existing maximum points of an attribute without overwriting the maximum points.
     * @param points the points to increase the current maximum points of the attribute
     */
    @Override
    public void increaseMaximum(Integer points) {
        this.maximumPoints += points;
        this.points = maximumPoints;
    }

    /**
     * Decreases the current maximum points of the attribute by the given points.
     * This will subtract from the existing maximum points of an attribute without overwriting the maximum points.
     * @param points the points to decrease the current maximum points of the attribute
     */
    @Override
    public void decreaseMaximum(Integer points) {
        this.maximumPoints -= points;
        this.points = Math.min(this.points, maximumPoints);
    }
}
