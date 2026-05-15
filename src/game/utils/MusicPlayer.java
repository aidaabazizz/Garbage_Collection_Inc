package game.utils;

import edu.monash.fit2099.engine.displays.Display;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Utility class used to play music files in the game.
 *
 * @author Suchir
 * @version 1.0
 */
public class MusicPlayer {

    /**
     * Plays a given .wav music file once.
     *
     * @param filePath the path of the music file
     */
    public static void playMusic(String filePath, Display display) {
        try {
            File musicFile = new File(filePath);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {
            display.println("Elsa tried to sing, but the music file could not be played.");
        }
    }
}