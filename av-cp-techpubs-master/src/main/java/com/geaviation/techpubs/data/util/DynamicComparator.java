package com.geaviation.techpubs.data.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DynamicComparator<T> implements Comparator<T> {

    private static final Logger log = LogManager.getLogger(DynamicComparator.class);
    private final int sortAscending;
    private final Method method;
    private final String compareType;

    public DynamicComparator(Class<?> sortClass, String sortColumn, boolean ascending) {
        this.sortAscending = (ascending ? 1 : -1);
        this.method = getMethod(sortClass, "get", sortColumn);
        this.compareType = (this.method == null ? "" : this.method.getReturnType().getName());
    }

    public int compare(T o1, T o2) {
        if (this.method == null) {
            return 0;
        }
        int rtnCd = 0;
        if (o1 == null && o2 == null) {
            rtnCd = 0;
        } else if (o1 == null || o2 == null) {
            rtnCd = (o1 == null ? -1 : 1);
        } else {
            try {
                if ("java.lang.String".equals(compareType)) {
                    rtnCd = compareString(o1, o2);

                } else if (("int".equals(compareType))) {
                    rtnCd = ((Integer) method.invoke(o1, (Object[]) null))
                        .compareTo((Integer) method.invoke(o2, (Object[]) null));
                }
            } catch (Exception e) {
                log.error("Exception" + e);
                return 0;
            }
        }

        return rtnCd * sortAscending;
    }

    private int compareString(T o1, T o2) throws IllegalAccessException, InvocationTargetException {
        int rtnCd;
        String s1 = ((String) method.invoke(o1, (Object[]) null));
        String s2 = ((String) method.invoke(o2, (Object[]) null));
        if (s1 == null && s2 == null) {
            rtnCd = 0;
        } else if (s1 == null) {
            rtnCd = -1;
        } else if (s2 == null) {
            rtnCd = 1;
        } else {
            rtnCd = s1.compareTo(s2);
        }
        return rtnCd;
    }

    private Method getMethod(Class<?> clazz, String prefix, String fieldName) {
        String methodName =
            prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return clazz.getMethod(methodName, (Class[]) null);
        } catch (Exception e) {
            log.error("Exception" + e);
        }
        return null;
    }

}
