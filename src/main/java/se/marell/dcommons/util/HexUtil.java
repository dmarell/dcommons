/*
 * Copyright (c) 1999,2011 Daniel Marell
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

/**
 * Utility class for converting a byte{} from and to Hex string.
 */
public class HexUtil {
  private HexUtil() {
  }

  /**
   * Turns array of bytes into string.
   *
   * @param buf Array of bytes to convert to hex string
   * @return Generated hex string
   */
  public static String asHex(byte[] buf) {
    StringBuilder sb = new StringBuilder(buf.length * 2);
    for (byte b : buf) {
      if (((int) b & 0xff) < 0x10) {
        sb.append("0");
      }
      sb.append(Integer.toString((int) b & 0xff, 16));
    }
    return sb.toString();
  }

  /**
   * Converts hex string to byte array.
   *
   * @param s String containing hex bytes. Length must pass isHexString().
   * @return Byte array
   */
  public static byte[] fromHex(String s) {
    if (!isHexString(s)) {
      throw new IllegalArgumentException("Invalid hex string:" + s);
    }
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  /**
   * Check if string is a valid hex byte string: Even length (empty is ok), only hex digits (0..9,a..f,A..F)
   *
   * @param s String to check
   * @return true if the string is valid
   */
  public static boolean isHexString(String s) {
    return s.isEmpty() || (s.length() % 2 == 0 && s.matches("\\p{XDigit}+"));
  }
}
