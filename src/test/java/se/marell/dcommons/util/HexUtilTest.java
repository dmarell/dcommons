/*
 * Created by Daniel Marell 12-07-06 11:05 AM
 */
package se.marell.dcommons.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HexUtilTest {
  @Test
  public void testAsHex() {
    byte[] arr = new byte[]{1, 0, -128, 0x7f};
    String s = HexUtil.asHex(arr);
    assertThat(s, is("0100807f"));
  }

  @Test
  public void testFromHex() {
    byte[] result;

    byte[] arr = new byte[]{1, 0, -128, 0x7f};
    result = HexUtil.fromHex("0100807f");
    assertThat(result, is(arr));

    result = HexUtil.fromHex("");
    assertThat(result, is(new byte[]{}));

    result = HexUtil.fromHex("020afb");
    assertThat(result, is(new byte[]{0x02, 0x0a, (byte) 0xfb}));

    result = HexUtil.fromHex("00000000");
    assertThat(result, is(new byte[]{0, 0, 0, 0}));
  }

  @Test
  public void testFromHexNegativeTests() {
    byte[] result;

    try {
      result = HexUtil.fromHex("1");
      assertThat(result, is(new byte[]{1}));
      fail();
    } catch (Exception ignore) {
    }

    try {
      result = HexUtil.fromHex("foobar");
      assertThat(result, is(new byte[]{1}));
      fail();
    } catch (Exception ignore) {
    }
  }

  @Test
  public void testIsHexString() {
    assertTrue(HexUtil.isHexString(""));
    assertFalse(HexUtil.isHexString("1"));
    assertTrue(HexUtil.isHexString("12"));
    assertFalse(HexUtil.isHexString("q1"));
    assertTrue(HexUtil.isHexString("0A"));
    assertTrue(HexUtil.isHexString("0a"));
    assertFalse(HexUtil.isHexString("0 000"));
  }
}
