package com.geaviation.techpubs.service.util;

import com.geaviation.techpubs.services.util.StringUtils;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void whenInputIsIntegerShouldReturnTrue(){
        String input = "1";
        assert(StringUtils.isInteger(input));
    }

    @Test
    public void whenInputIsNullShouldReturnTrue(){
        assert(StringUtils.isInteger(null));
    }

    @Test
    public void whenInputIsNotIntegerShouldReturnFalse(){
        String input = "some text";
        assert(!StringUtils.isInteger(input));
    }

}
