package com.xzf.blog.article.biz.util;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownStatsUtilTest {

    @Test
    void shouldCalculateWordCountForMixedChineseAndEnglish() {
        String markdown = "你好 world  this is  \n a test";

        assertEquals(7, MarkdownStatsUtil.calculateWordCount(markdown));
    }

    @Test
    void shouldCalculateWordCountForBlankText() {
        assertEquals(0, MarkdownStatsUtil.calculateWordCount("   \n\t  "));
    }

    @Test
    void shouldCalculateReadingTimeInMinutesBranch() {
        String readingTime = MarkdownStatsUtil.calculateReadingTime(600);

        assertEquals(2, extractFirstNumber(readingTime));
    }

    @Test
    void shouldCalculateReadingTimeInSecondsBranchAndKeepMinimumOneSecond() {
        String readingTime = MarkdownStatsUtil.calculateReadingTime(0);

        assertEquals(1, extractFirstNumber(readingTime));
    }

    private int extractFirstNumber(String input) {
        Matcher matcher = Pattern.compile("\\d+").matcher(input);
        if (!matcher.find()) {
            return -1;
        }
        return Integer.parseInt(matcher.group());
    }
}
