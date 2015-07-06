/*
 * Created by Daniel Marell 2011-10-13 20:32
 */
package se.marell.dcommons.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a playback output device.
 */
public class SoundPlayerDevice {
  private static final AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                            AudioSystem.NOT_SPECIFIED,
                                                            16, 2, 4,
                                                            AudioSystem.NOT_SPECIFIED, true);

  private String playerDeviceName;
  private Mixer mixer;

  public SoundPlayerDevice(String playerDeviceName, Mixer mixer) {
    this.playerDeviceName = playerDeviceName;
    this.mixer = mixer;
  }

  public SoundPlayerLine getLine(File soundFile, boolean force) throws AudioException {
    try {
      Clip clip = AudioSystem.getClip(mixer.getMixerInfo());
      return new SoundPlayerLine(clip, soundFile, force);
    } catch (LineUnavailableException e) {
      return null;
    }
  }

  public int getMaxLines() {
    return mixer.getMaxLines(new DataLine.Info(Clip.class, format));
  }

  public static SoundPlayerDevice createSoundPlayerDevice(String deviceNamePattern) {
    DataLine.Info info = new DataLine.Info(Clip.class, format);
    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
      Mixer mixer = AudioSystem.getMixer(mi);
      if (mixer.isLineSupported(info) && mi.getName().equals(deviceNamePattern) || mi.getName().matches(deviceNamePattern)) {
        return new SoundPlayerDevice(mi.getName(), mixer);
      }
    }
    return null;
  }


  public static List<String> getPlayerDeviceNames() {
    List<String> deviceNames = new ArrayList<String>();
    DataLine.Info info = new DataLine.Info(Clip.class, format);
    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
      Mixer mixer = AudioSystem.getMixer(mi);
      if (mixer.isLineSupported(info)) {
        deviceNames.add(mi.getName());
      }
    }
    return deviceNames;
  }

  @Override
  public String toString() {
    return playerDeviceName;
  }
}
