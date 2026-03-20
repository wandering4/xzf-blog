package com.xzf.blog.framework.commons.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneNumberValidatorTest {

    private final PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    void shouldAccept11Digits() {
        assertTrue(validator.isValid("13800138000", null));
    }

    @Test
    void shouldRejectNullOrNot11Digits() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("1380013800", null));
        assertFalse(validator.isValid("138001380000", null));
        assertFalse(validator.isValid("13800138abc", null));
    }
}
