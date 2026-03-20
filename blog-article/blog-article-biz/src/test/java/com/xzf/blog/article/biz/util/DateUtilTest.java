package com.xzf.blog.article.biz.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateUtilTest {

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(DateUtil.convertToLocalDate(null));
    }

    @Test
    void shouldConvertLocalDate() {
        LocalDate date = LocalDate.of(2026, 3, 20);

        assertEquals(date, DateUtil.convertToLocalDate(date));
    }

    @Test
    void shouldConvertSqlDate() {
        LocalDate date = LocalDate.of(2026, 3, 20);
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        assertEquals(date, DateUtil.convertToLocalDate(sqlDate));
    }

    @Test
    void shouldConvertUtilDate() {
        LocalDate date = LocalDate.of(2026, 3, 20);
        Date utilDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        assertEquals(date, DateUtil.convertToLocalDate(utilDate));
    }

    @Test
    void shouldConvertStringDate() {
        assertEquals(LocalDate.of(2026, 3, 20), DateUtil.convertToLocalDate("2026-03-20"));
    }

    @Test
    void shouldReturnNullForInvalidInput() {
        assertNull(DateUtil.convertToLocalDate("2026-13-20"));
        assertNull(DateUtil.convertToLocalDate(123L));
    }
}
