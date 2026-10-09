package com.campus.mapper;

import com.campus.entity.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper {
    List<Notice> findPage(@Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    long count(@Param("keyword") String keyword);

    List<Notice> findLatest(@Param("limit") int limit);

    Notice findById(@Param("id") Integer id);

    int insert(Notice notice);

    int update(Notice notice);

    int deleteById(@Param("id") Integer id);
}
