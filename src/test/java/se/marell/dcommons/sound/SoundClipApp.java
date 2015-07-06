/*
 * Created by Daniel Marell 2011-10-13 20:51
 */
package se.marell.dcommons.sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SoundClipApp {
  private File soundFile;
  private List<String> playbackDevices;
  private List<JRadioButton> radioButtonList;
  private Map<String, SoundPlayerDevice> soundDeviceMap = new HashMap<String, SoundPlayerDevice>();

  public SoundClipApp(File soundFile) {
    this.soundFile = soundFile;

    playbackDevices = SoundPlayerDevice.getPlayerDeviceNames();
    for (String device : playbackDevices) {
      SoundPlayerDevice playerDevice = SoundPlayerDevice.createSoundPlayerDevice(device);
      soundDeviceMap.put(device, playerDevice);
    }
  }

  protected void start() {
    final SoundClip player = createPlayer();

    final JPanel playbackDevicesPanel = createPlaybackDevicesPanel();

    final JSlider volumeSlider = new JSlider(0, 100);
    volumeSlider.setValue(100);

    JButton startBtn = new JButton("Ring");
    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        float vol = volumeSlider.getValue() / 100f;
        try {
          String device = getPlaybackDevice();
          System.out.printf("Calling play, device=%s, vol=%.2f\n", device, vol);

          SoundPlayerDevice playerDevice = soundDeviceMap.get(device);
          if (playerDevice != null) {
            player.play(playerDevice, vol);
          } else {
            System.out.printf("Failed to find device=%s\n", device);
          }
        } catch (AudioException e1) {
          e1.printStackTrace();
        }
        System.out.println("Called play");
      }
    });
    JButton stopBtn = new JButton("Stop");
    stopBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Calling stop");
        player.stop();
        System.out.println("Called stop");
      }
    });

    JPanel p = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    p.add(startBtn, c);
    p.add(stopBtn, c);
    p.add(volumeSlider, c);
    c.gridy = 1;
    p.add(playbackDevicesPanel, c);

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setContentPane(p);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private String getPlaybackDevice() {
    int i = 0;
    for (JRadioButton rb : radioButtonList) {
      if (rb.isSelected()) {
        return playbackDevices.get(i);
      }
      ++i;
    }
    return null;
  }

  private JPanel createPlaybackDevicesPanel() {
    final JPanel p = new JPanel(new GridBagLayout());

    final JButton refreshBtn = new JButton("Refresh");
    refreshBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        p.removeAll();
        addRadioButtons(p, refreshBtn);
      }
    });

    addRadioButtons(p, refreshBtn);

    return p;
  }

  private void addRadioButtons(JPanel p, JButton btn) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridy = 1;
    c.gridwidth = 3;
    c.anchor = GridBagConstraints.WEST;
    radioButtonList = new ArrayList<JRadioButton>();
    ButtonGroup group = new ButtonGroup();
    for (String device : playbackDevices) {
      System.out.println("device=" + device);
      JRadioButton b = new JRadioButton(device);
      p.add(b, c);
      group.add(b);
      radioButtonList.add(b);
      c.gridy++;
    }
    p.add(btn, c);
  }

  protected SoundClip createPlayer() {
    return new SoundClip(soundFile,
                         new SoundClip.Listener() {
                           @Override
                           public void ready() {
                             System.out.println("play finished");
                           }
                         });
  }

  public static void main(String[] args) {
    List<String> deviceNames = SoundPlayerDevice.getPlayerDeviceNames();
    System.out.println("deviceNames:");
    for (String deviceName : deviceNames) {
      System.out.println("  deviceName=" + deviceName);
    }
    System.out.println();

    File soundFile = new File(args[0]);
    SoundClipApp app = new SoundClipApp(soundFile);
    app.start();
  }
}
