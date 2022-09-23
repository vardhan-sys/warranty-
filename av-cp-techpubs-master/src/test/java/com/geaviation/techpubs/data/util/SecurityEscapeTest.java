package com.geaviation.techpubs.data.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SecurityEscapeTest {

  public static class cleanStringTest {

    String stringWithDoubleWhitespace = "Test  String";

    @Test
    public void whenStringHasDoubleWhitespaceItIsPreserved(){
      assertEquals(stringWithDoubleWhitespace, SecurityEscape.cleanString(stringWithDoubleWhitespace));
    }

  }

}
