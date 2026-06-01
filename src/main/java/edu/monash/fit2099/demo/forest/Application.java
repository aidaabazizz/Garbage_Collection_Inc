package edu.monash.fit2099.demo.forest;

import edu.monash.fit2099.engine.displays.Display;

public class Application {

    public static void main(String[] args) {
        Display terminalDisplay = new Display();
        Earth earth = new Earth(terminalDisplay);
        try{
            earth.constructWorld();
            earth.run();
        }
        catch (Exception e) {
            // General exception, to help debugging.
            e.printStackTrace();
        }

    }

}
