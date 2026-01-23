package com.xzf.blog.article.biz.util;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateUtil {
    public static LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj == null) {
            return null;
        }

        try {
            if (dateObj instanceof LocalDate) {
                return (LocalDate) dateObj;
            } else if (dateObj instanceof java.sql.Date) {
                // java.sql.Date -> LocalDate
                return ((java.sql.Date) dateObj).toLocalDate();
            } else if (dateObj instanceof java.util.Date) {
                // java.util.Date -> LocalDate
                return ((java.util.Date) dateObj).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } else if (dateObj instanceof String) {
                // String -> LocalDate
                return LocalDate.parse((String) dateObj);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
