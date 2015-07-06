/*
 * Created by Daniel Marell 12-07-01 9:51 PM
 */
package se.marell.dcommons.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.marell.dcommons.util.SettingsFile;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SettingsFileTest {
  private File file = new File("deleteme");

  @Before
  public void setup() {
    file.delete();
  }

  @After
  public void cleanup() {
    file.delete();
  }

  @Test
  public void testPutGet() throws Exception {
    file.delete();

    SettingsFile sf = new SettingsFile(file);
    assertNull(sf.getProperty("foo"));
    sf.putProperty("foo", "bar");
    assertThat(sf.getProperty("foo"), is("bar"));
    sf.putProperty("foo", null);
    assertNull(sf.getProperty("foo"));
    sf.putProperty("foo", "bar");

    sf = new SettingsFile(file);
    assertThat(sf.getProperty("foo"), is("bar"));
  }
}
