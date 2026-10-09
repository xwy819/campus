package com.campus.service;

import com.campus.mapper.OrderMapper;
import com.campus.mapper.ProductMapper;
import com.campus.mapper.StatsMapper;
import com.campus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务：为前端图表提供数据。
 */
@Service
public class StatsService {

    @Autowired
    private StatsMapper statsMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("productCnt", productMapper.countByStatus(1));   // 在售商品
        data.put("userCnt", userMapper.count());                   // 用户数
        data.put("orderCnt", orderMapper.countByStatus(2));        // 已完成订单
        data.put("pendingCnt", productMapper.countByStatus(0));    // 待审核商品
        data.put("amount", orderMapper.sumAmount(2));              // 成交总额
        return data;
    }

    public List<Map<String, Object>> categoryStats() {
        return statsMapper.categoryStats();
    }

    public Map<String, Object> priceLevel() {
        Map<String, Object> data = statsMapper.priceLevel();
        // 防除零
        long total = data.get("total") == null ? 0 : ((Number) data.get("total")).longValue();
        if (total == 0) {
            data.put("lowPct", 0.0);
            data.put("midPct", 0.0);
            data.put("highPct", 0.0);
        } else {
            data.put("lowPct", round(((Number) data.get("low_cnt")).longValue() * 100.0 / total));
            data.put("midPct", round(((Number) data.get("mid_cnt")).longValue() * 100.0 / total));
            data.put("highPct", round(((Number) data.get("high_cnt")).longValue() * 100.0 / total));
        }
        return data;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
