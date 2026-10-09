package com.campus.mapper;

import java.util.List;
import java.util.Map;

public interface StatsMapper {
    /** 各分类商品数量与平均/最高/最低价 */
    List<Map<String, Object>> categoryStats();

    /** 价格档位分布 */
    Map<String, Object> priceLevel();
}
