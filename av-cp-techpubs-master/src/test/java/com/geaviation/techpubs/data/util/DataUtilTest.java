package com.geaviation.techpubs.data.util;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DataUtilTest {

  public static class IsSBAlertTest{

    @Test
    public void whenSbTypeIsNullThenShouldReturnFalse(){
      assertFalse(DataUtil.isSBAlert(null));
    }

    @Test
    public void whenSbTypeIsEmptyThenShouldReturnFalse(){
      assertFalse(DataUtil.isSBAlert(""));
    }

    @Test
    public void whenSbTypeIsNotAlertOrAlertCoverThenShouldReturnFalse(){
      assertFalse(DataUtil.isSBAlert("test"));
    }

    @Test
    public void whenSbTypeIsAlertThenShouldReturnTrue(){
      assert(DataUtil.isSBAlert(DataConstants.ALERT));
    }

    @Test
    public void whenSbTypeIsAlertUpperCaseThenShouldReturnTrue(){
      assert(DataUtil.isSBAlert(DataConstants.ALERT.toUpperCase()));
    }

    @Test
    public void whenSbTypeIsAlertCoverThenShouldReturnTrue(){
      assert(DataUtil.isSBAlert(DataConstants.ALERT_COVER));
    }

    @Test
    public void whenSbTypeIsAlertCoverUppercaseThenShouldReturnTrue(){
      assert(DataUtil.isSBAlert(DataConstants.ALERT_COVER.toUpperCase()));
    }

  }

}
