package com.geaviation.techpubs.services.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public abstract class StringUtils {

    private static final Logger log = LogManager.getLogger(StringUtils.class);

    private StringUtils() {

    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    public static boolean containsWhitespace(String str) {

        if (isEmpty(str)) {
            return false;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String trimWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    // Test if the given String starts with the specified prefix
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    // Test if the given String starts with the specified prefix
    public static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }

        String testStr = (ignoreCase ? str.substring(0, prefix.length()).toLowerCase()
            : str.substring(0, prefix.length()));
        String testPrefix = (ignoreCase ? prefix.toLowerCase() : prefix);

        return testStr.equals(testPrefix);
    }

    // Test if the given String ends with the specified suffix
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    // Test if the given String ends with the specified suffix
    public static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String testStr = (ignoreCase ? str.substring(str.length() - suffix.length()).toLowerCase()
            : str.substring(str.length() - suffix.length()));
        String testSuffix = (ignoreCase ? suffix.toLowerCase() : suffix);

        return testStr.equals(testSuffix);
    }

    public static boolean isInteger(String input) {
        if (input == null) {
            return true;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            log.error(e);
            return false;
        }
    }

    public static boolean isDate(String input, String format) {
        if (input == null) {
            return true;
        }
        if (format == null) {
            return false;
        }
        if (input.length() != format.length()) {
            return false;
        }
        try {
            ParsePosition position = new ParsePosition(0);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(input, position);
            return (position.getIndex() == input.length());
        } catch (Exception e) {
            log.error(e);
            return false;
        }

    }

    public static String convertDateFormat(String input, String inFormat, String outFormat) {
        if (input == null || input.length() < 1) {
            return null;
        }
        try {
            SimpleDateFormat sdfIn = new SimpleDateFormat(inFormat);
            SimpleDateFormat sdfOut = new SimpleDateFormat(outFormat);
            return sdfOut.format(sdfIn.parse(input));
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for (String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    public static int monthAsInt(String strMonth) {
        if (strMonth == null) {
            return 0;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("MMM").parse(strMonth));
            return cal.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            log.error(e);
            return 0;
        }
    }

    public static String getFormattedTimestamp(String fmt) {
        return new SimpleDateFormat(fmt).format(new Date());
    }

    public static String listToString(List<String> strings) {
        if (strings.size() == 1) {
            return strings.get(0);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strings.size() - 1; i++) {
                sb.append(strings.get(0));
                sb.append(", ");
            }
            sb.append(strings.get(strings.size() - 1));
            return sb.toString();
        }

    }
}
