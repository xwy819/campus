package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计接口（为前端图表提供数据）。
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(statsService.dashboard());
    }

    @GetMapping("/category")
    public Result<List<Map<String, Object>>> category() {
        return Result.success(statsService.categoryStats());
    }

    @GetMapping("/price")
    public Result<Map<String, Object>> price() {
        return Result.success(statsService.priceLevel());
    }
}
