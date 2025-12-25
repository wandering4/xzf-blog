package com.xzf.blog.framework.commons.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @description: 分页响应参数工具类
 */
@Data
public class PageResponse<T> extends Response<List<T>> {

    private long pageNo; // 当前页码
    private long totalCount; // 总数据量
    private long pageSize; // 每页展示的数据量
    private long totalPage; // 总页数

    /**
     * 成功响应
     * @param page Mybatis Plus 提供的分页接口
     * @param data
     * @return
     * @param <T>
     */
    public static <T> PageResponse<T> success(IPage page, List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setSuccess(true);
        response.setPageNo(Objects.isNull(page) ? 1L : page.getCurrent());
        response.setPageSize(Objects.isNull(page) ? 10L : page.getSize());
        response.setTotalPage(Objects.isNull(page) ? 0L : page.getPages());
        response.setTotalCount(Objects.isNull(page) ? 0L : page.getTotal());
        response.setData(data);
        return response;
    }

    public static <T> PageResponse<T> success(List<T> data, long pageNo, long totalCount) {
        return success(data, pageNo, totalCount, 10);
    }

    public static <T> PageResponse<T> success(List<T> data, long pageNo, long totalCount, long pageSize) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setData(data);
        pageResponse.setPageNo(pageNo);
        pageResponse.setTotalCount(totalCount);
        pageResponse.setPageSize(pageSize);
        // 计算总页数
        long totalPage = pageSize == 0 ? 0 : (totalCount + pageSize - 1) / pageSize;
        pageResponse.setTotalPage(totalPage);
        return pageResponse;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public static long getTotalPage(long totalCount, long pageSize) {
        return pageSize == 0 ? 0 : (totalCount + pageSize - 1) / pageSize;
    }

    /**
     * 计算分页查询的 offset
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static long getOffset(long pageNo, long pageSize) {
        // 如果页码小于 1，默认返回第一页的 offset
        if (pageNo < 1) {
            pageNo = 1;
        }
        return (pageNo - 1) * pageSize;
    }

}
