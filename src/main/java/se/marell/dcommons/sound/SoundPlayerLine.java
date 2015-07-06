/*
 * Created by Daniel Marell 2011-10-13 20:31
 */
package se.marell.dcommons.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Represents an output line on a playback output device.
 */
public class SoundPlayerLine {
  private Clip clip;
  private SoundClip.Listener listener;

  public SoundPlayerLine(Clip clip, File soundFile, boolean force) throws AudioException {
    this.clip = clip;
    open(soundFile, force);
  }

  private void open(File soundFile, boolean force) throws AudioException {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
      clip.open(audioInputStream);

      clip.addLineListener(new LineListener() {
        public void update(LineEvent event) {
          if (event.getType() == LineEvent.Type.STOP) {
            clip.close();
            if (listener != null) {
              listener.ready();
            }
          }
        }
      });

    } catch (LineUnavailableException e) {
      if (force) {
        clip.close();
        try {
          open(soundFile, false);
        } catch (AudioException e1) {
          throw new AudioException("reopen failed:" + e.getMessage());
        }
      }
    } catch (IOException e) {
      throw new AudioException(e.getMessage());
    } catch (UnsupportedAudioFileException e) {
      throw new AudioException(e.getMessage());
    }
  }

  public void close() {
    clip.close();
  }

  public void setVolume(float volume) {
    FloatControl ctrl = null;
    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
      ctrl = (FloatControl) (clip.getControl(FloatControl.Type.MASTER_GAIN));
    } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
      ctrl = (FloatControl) (clip.getControl(FloatControl.Type.VOLUME));
    }
    if (ctrl != null) {
      float value = (ctrl.getMaximum() - ctrl.getMinimum()) * volume + ctrl.getMinimum();
      ctrl.setValue(value);
    }
  }

  public void play() {
    play(null);
  }

  public void stop() {
    clip.close();
  }

  public void play(SoundClip.Listener listener) {
    this.listener = listener;
    clip.setFramePosition(0);
    clip.start();
  }
}
