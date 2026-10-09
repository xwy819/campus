package com.campus.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {
    private Long total;
    private List<T> records;
    private Integer pageNum;
    private Integer pageSize;

    public static <T> PageResult<T> of(Long total, List<T> records, Integer pageNum, Integer pageSize) {
        PageResult<T> p = new PageResult<>();
        p.total = total;
        p.records = records;
        p.pageNum = pageNum;
        p.pageSize = pageSize;
        return p;
    }
}
