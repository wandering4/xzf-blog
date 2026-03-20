package com.xzf.blog.framework.commons.response;

import com.xzf.blog.framework.commons.exception.BaseExceptionInterface;
import com.xzf.blog.framework.commons.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

    @Test
    void successWithoutDataShouldKeepSuccessTrue() {
        Response<Object> response = Response.success();

        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }

    @Test
    void successWithDataShouldContainPayload() {
        Response<String> response = Response.success("ok");

        assertTrue(response.isSuccess());
        assertEquals("ok", response.getData());
    }

    @Test
    void failOverloadsShouldSetFields() {
        Response<Object> fail = Response.fail();
        Response<Object> failWithMsg = Response.fail("bad request");
        Response<Object> failWithCode = Response.fail("E001", "bad request");

        assertFalse(fail.isSuccess());

        assertFalse(failWithMsg.isSuccess());
        assertEquals("bad request", failWithMsg.getMessage());

        assertFalse(failWithCode.isSuccess());
        assertEquals("E001", failWithCode.getErrorCode());
        assertEquals("bad request", failWithCode.getMessage());
    }

    @Test
    void failWithBizExceptionShouldCopyErrorCodeAndMessage() {
        BizException bizException = new BizException(TestError.TEST);

        Response<Object> response = Response.fail(bizException);

        assertFalse(response.isSuccess());
        assertEquals("T001", response.getErrorCode());
        assertEquals("test error", response.getMessage());
    }

    @Test
    void failWithBaseExceptionShouldCopyErrorCodeAndMessage() {
        Response<Object> response = Response.fail(TestError.TEST);

        assertFalse(response.isSuccess());
        assertEquals("T001", response.getErrorCode());
        assertEquals("test error", response.getMessage());
    }

    private enum TestError implements BaseExceptionInterface {
        TEST;

        @Override
        public String getErrorCode() {
            return "T001";
        }

        @Override
        public String getErrorMessage() {
            return "test error";
        }
    }
}
