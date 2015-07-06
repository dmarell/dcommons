/*
 * Copyright (c) 2010,2011 Daniel Marell
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package se.marell.dcommons.util;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements ApplicationSettings using a plain text file. The whole file is written each time a single
 * property is written. The file cannot be updated externally, as it will be over-written by this class next time
 * a property changes.
 */
public class SettingsFile implements ApplicationSettings {
  public class FormatException extends Exception {
    public FormatException(String message) {
      super(message);
    }
  }

  private File file;
  private Map<String, String> props = new HashMap<String, String>();

  public SettingsFile(File file) throws FormatException {
    this.file = file;
    readFromFile();
  }

  /**
   * Read properties, keys and valued, from file. The file replaces any
   * eventual previously cached properties.
   *
   * @throws FormatException If file parsing failed
   */
  public void readFromFile() throws FormatException {
    props.clear();

    // Populate map from file
    BufferedReader reader = null;

    int lineNo = 0;
    String line;

    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

      while ((line = reader.readLine()) != null) {
        ++lineNo;
        line = line.trim();
        if (line.length() == 0 || line.charAt(0) == '#')
          continue;

        int pos = line.indexOf('=');
        if (pos < 0) {
          throw new FormatException(file.getAbsolutePath() + ":Expected '=' at line " + lineNo);
        }
        String key = line.substring(0, pos);
        String value = line.substring(pos + 1);
        if (props.containsKey(key)) {
          throw new FormatException(file.getAbsolutePath() + ":duplicate key " + key);
        }
        props.put(key, value);
      }
    } catch (FileNotFoundException e) {
      // Normal case
    } catch (IOException e) {
      throw new FormatException("Failed to open property file, starting empty (" + e.getMessage() + ')');
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignore) {
        }
      }
    }
  }

  /**
   * Write properties, keys and valued, to file. Any existing file is overwritten.
   *
   * @throws IOException If file write failed
   */
  public void writeToFile() throws IOException {
    // Write map to file
    PrintWriter out = null;

    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
      out.println("# Property file used by the application to read and write property values while running.");
      out.println("# Last write: " + Calendar.getInstance().getTime());
      out.println();
      for (Map.Entry entry : props.entrySet()) {
        out.println(entry.getKey() + "=" + entry.getValue());
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  @Override
  public String getProperty(String key) {
    return getProperty(key, null);
  }

  @Override
  public String getProperty(String key, String defaultValue) {
    String value = props.get(key);
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  @Override
  public void putProperty(String key, String value) throws IOException {
    props.put(key, value);
    writeToFile();
  }
}
