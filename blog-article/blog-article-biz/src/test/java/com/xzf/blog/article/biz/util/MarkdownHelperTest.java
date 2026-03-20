package com.xzf.blog.article.biz.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownHelperTest {

    @Test
    void shouldRenderExternalLinkWithNofollowAndBlankTarget() {
        String html = MarkdownHelper.convertMarkdown2Html("[OpenAI](https://openai.com)");

        assertTrue(html.contains("target=\"_blank\""));
        assertTrue(html.contains("ref=\"nofollow\""));
        assertTrue(html.contains(">OpenAI</a>"));
    }

    @Test
    void shouldRenderInternalLinkWithoutNofollow() {
        String html = MarkdownHelper.convertMarkdown2Html("[local](http://127.0.0.1:8000/docs)");

        assertTrue(html.contains("target=\"_blank\""));
        assertFalse(html.contains("ref=\"nofollow\""));
    }

    @Test
    void shouldRenderImageAndCaption() {
        String html = MarkdownHelper.convertMarkdown2Html("![cover](https://img.test/pic.png \"cover-title\")");

        assertTrue(html.contains("<img src=\"https://img.test/pic.png\""));
        assertTrue(html.contains("title=\"cover-title\""));
        assertTrue(html.contains("class=\"image-caption\">cover-title</span>"));
    }
}
