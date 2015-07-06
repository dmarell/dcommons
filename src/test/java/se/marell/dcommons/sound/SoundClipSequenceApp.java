/*
 * Created by Daniel Marell 2011-10-13 23:28
 */
package se.marell.dcommons.sound;

import java.io.File;
import java.util.List;

public class SoundClipSequenceApp {
  public static void main(String[] args) {
    List<String> deviceNames = SoundPlayerDevice.getPlayerDeviceNames();
    System.out.println("deviceNames:");
    for (String deviceName : deviceNames) {
      System.out.println("  deviceName=" + deviceName);
    }
    System.out.println();

    testSequence();
  }

  private static void testSequence() {
    SoundClip clip1 = new SoundClip(new File("aieeh.wav"));
    SoundClip clip2 = new SoundClip(new File("bang1.wav"));
    final int delay = 600;

    SoundPlayerDevice device1 = SoundPlayerDevice.createSoundPlayerDevice("Intel.*");
    SoundPlayerDevice device2 = SoundPlayerDevice.createSoundPlayerDevice("Device.*");

    try {
      System.out.println("playing clip1 device1");
      clip1.play(device1);
      System.out.println("played clip1 device1");
    } catch (AudioException e) {
      System.out.println("clip1 device1 failed:" + e.getMessage());
    }
    sleep(delay);

    try {
      System.out.println("playing clip2 device1");
      clip2.play(device1);
      System.out.println("played clip2 device1");
    } catch (AudioException e) {
      System.out.println("clip2 device1 failed:" + e.getMessage());
    }
    sleep(delay);

    try {
      System.out.println("playing clip2 device2");
      clip2.play(device2);
      System.out.println("played clip2 device2");
    } catch (AudioException e) {
      System.out.println("clip2 device2 failed:" + e.getMessage());
    }
    sleep(delay);

    try {
      System.out.println("playing clip1 device2");
      clip1.play(device2);
      System.out.println("played clip1 device2");
    } catch (AudioException e) {
      System.out.println("clip1 device2 failed:" + e.getMessage());
    }
    sleep(delay);

    sleep(2000);
  }

  private static void sleep(long t) {
    if (t > 0) {
      try {
        Thread.sleep(t);
      } catch (InterruptedException ignore) {
      }
    }
  }

}
