package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Notice;
import com.campus.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告接口。
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/page")
    public Result<PageResult<Notice>> page(@RequestParam(required = false) String keyword,
                                           @RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(noticeService.page(keyword, pageNum, pageSize));
    }

    @GetMapping("/latest")
    public Result<List<Notice>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(noticeService.latest(limit));
    }

    @GetMapping("/detail")
    public Result<Notice> detail(@RequestParam Integer id) {
        return Result.success(noticeService.detail(id));
    }

    @PostMapping("/admin")
    public Result<?> publish(@RequestBody Notice notice) {
        return noticeService.publish(notice);
    }

    @PutMapping("/admin")
    public Result<?> update(@RequestBody Notice notice) {
        return noticeService.update(notice);
    }

    @DeleteMapping("/admin/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return noticeService.delete(id);
    }
}
