package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Notice;
import com.campus.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告业务服务。
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public PageResult<Notice> page(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = noticeMapper.count(keyword);
        List<Notice> records = noticeMapper.findPage(keyword, offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    public List<Notice> latest(int limit) {
        return noticeMapper.findLatest(limit);
    }

    public Notice detail(Integer id) {
        return noticeMapper.findById(id);
    }

    public Result<?> publish(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return Result.error("公告标题不能为空");
        }
        AuthContext ctx = AuthContext.get();
        notice.setAdminId(ctx.getUserId());
        noticeMapper.insert(notice);
        return Result.success();
    }

    public Result<?> update(Notice notice) {
        noticeMapper.update(notice);
        return Result.success();
    }

    public Result<?> delete(Integer id) {
        noticeMapper.deleteById(id);
        return Result.success();
    }
}
