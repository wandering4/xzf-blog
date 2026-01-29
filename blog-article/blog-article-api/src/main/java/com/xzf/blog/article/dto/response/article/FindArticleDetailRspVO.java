package com.xzf.blog.article.dto.response.article;

import com.xzf.blog.article.dto.response.tag.FindTagListRspVO;
import com.xzf.blog.article.dto.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindArticleDetailRspVO {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 作者信息
     */
    private UserInfoVO authorInfo;
    /**
     * 文章正文（HTML）
     */
    private String content;
    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 分类 ID
     */
    private Long categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 阅读量
     */
    private Long readNum;

    /**
     * 评论量
     */
    private Long commentNum;
    /**
     * 标签集合
     */
    private List<FindTagListRspVO> tags;

    /**
     * 总字数
     */
    private Integer totalWords;

    /**
     * 阅读时长
     */
    private String readTime;
}

