/*
 * Created by Daniel Marell 2011-10-13 20:49
 */
package se.marell.dcommons.sound;

import java.io.File;

/**
 * Represents a sound clip in the form of a sound file.
 */
public class SoundClip {

  public interface Listener {
    /**
     * Called when playing the clip is ready.
     */
    void ready();
  }

  private File soundFile;
  private Listener listener;
  private SoundPlayerLine line;

  public SoundClip(File soundFile) {
    this.soundFile = soundFile;
  }

  public SoundClip(File soundFile, Listener listener) {
    this.soundFile = soundFile;
    this.listener = listener;
  }

  public void play(SoundPlayerDevice device) throws AudioException {
    play(device, 1f);
  }

  public void play(SoundPlayerDevice device, float volume) throws AudioException {
    line = device.getLine(soundFile, true);
    line.setVolume(volume);
    line.play(listener);
  }

  public void stop() {
    if (line != null) {
      line.stop();
    }
  }
}

