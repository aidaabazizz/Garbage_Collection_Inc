package game;

import edu.monash.fit2099.engine.displays.Display;

/**
 * The starting point of the game. Think of it as the Big Bang, but written in Java.
 * It also prints the game banner, because every new universe deserves a dramatic title sequence.
 * Without this class, your game is just a collection of highly ambitious, unemployed objects.
 *
 * @see FancyMessage
 * @see EclipseNebula
 * @author Adrian Kristanto
 */
public class Application {

    /**
     * The main method that runs the game, prints the game banner, and catches any exception being thrown by the game.
     */
    public static void main(String[] args) {
        Display display = new Display();
        EclipseNebula eclipseNebula = new EclipseNebula(display);
        try{
            for (String line : FancyMessage.GAME_TITLE.split("\n")) {
                new Display().println(line);
                try {
                    Thread.sleep(200);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            eclipseNebula.initialise();
            eclipseNebula.run();
        }
        catch (Exception e) {
            // General exception, to help debugging.
            e.printStackTrace();
        }
    }
}
