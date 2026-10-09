package com.campus.controller;

import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Order;
import com.campus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单接口。
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public Result<PageResult<Order>> page(@RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.myPage(status, pageNum, pageSize));
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, Object> body) {
        Integer productId = ((Number) body.get("productId")).intValue();
        Integer deliveryType = body.get("deliveryType") != null ? ((Number) body.get("deliveryType")).intValue() : 1;
        Integer addressId = body.get("addressId") != null ? ((Number) body.get("addressId")).intValue() : null;
        return orderService.create(productId, deliveryType, addressId);
    }

    @PostMapping("/status")
    public Result<?> changeStatus(@RequestBody Map<String, Integer> body) {
        return orderService.changeStatus(body.get("id"), body.get("status"));
    }

    @GetMapping("/admin/page")
    public Result<PageResult<Order>> adminPage(@RequestParam(required = false) Integer status,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.adminPage(status, pageNum, pageSize));
    }
}
