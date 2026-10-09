package com.campus.service;

import com.campus.common.AuthContext;
import com.campus.common.PageResult;
import com.campus.common.Result;
import com.campus.entity.Message;
import com.campus.entity.Order;
import com.campus.entity.Product;
import com.campus.mapper.MessageMapper;
import com.campus.mapper.OperationLogMapper;
import com.campus.entity.OperationLog;
import com.campus.mapper.OrderMapper;
import com.campus.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单业务服务。
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;
    @Autowired
    private MessageMapper messageMapper;

    /** 我的订单（买家或卖家视角） */
    public PageResult<Order> myPage(Integer status, int pageNum, int pageSize) {
        AuthContext ctx = AuthContext.get();
        int offset = (pageNum - 1) * pageSize;
        // 查询当前用户作为买家或卖家的所有订单
        long total = orderMapper.count(ctx.getUserId(), null, null, status);
        List<Order> records = orderMapper.findPage(ctx.getUserId(), null, null, status, offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    /** 管理员分页 */
    public PageResult<Order> adminPage(Integer status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        long total = orderMapper.count(null, null, null, status);
        List<Order> records = orderMapper.findPage(null, null, null, status, offset, pageSize);
        return PageResult.of(total, records, pageNum, pageSize);
    }

    /** 买家下单 */
    @Transactional
    public Result<?> create(Integer productId, Integer deliveryType, Integer addressId) {
        AuthContext ctx = AuthContext.get();
        Product product = productMapper.findById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (product.getStatus() != 1) {
            return Result.error("商品不在售，无法下单");
        }
        if (product.getUserId().equals(ctx.getUserId())) {
            return Result.error("不能购买自己发布的商品");
        }
        // 校验交易方式是否在卖家支持范围内
        int supported = product.getDeliveryType() != null ? product.getDeliveryType() : 1;
        if ((supported & deliveryType) == 0) {
            return Result.error("卖家不支持该交易方式");
        }
        // 送货上门必须选择地址
        if (deliveryType == 2 && addressId == null) {
            return Result.error("请选择收货地址");
        }
        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(ctx.getUserId());
        order.setSellerId(product.getUserId());
        order.setStatus(0); // 待卖家确认
        order.setFinalPrice(product.getPrice());
        order.setDeliveryType(deliveryType);
        order.setAddressId(addressId);
        orderMapper.insert(order);
        // 商品变为已售
        productMapper.updateStatus(productId, 2);
        // 给卖家发送站内消息：下单请求通知
        Message msg = new Message();
        msg.setOrderId(order.getId());
        msg.setSenderId(ctx.getUserId());
        msg.setReceiverId(product.getUserId());
        msg.setContent("您发布的商品《" + product.getTitle() + "》收到新的下单请求，请及时处理。");
        messageMapper.insert(msg);
        operationLogMapper.insert(newLog("下单商品 id=" + productId));
        return Result.success();
    }

    /** 确认收货 / 完成 / 取消 等状态流转 */
    public Result<?> changeStatus(Integer id, Integer status) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        AuthContext ctx = AuthContext.get();
        // 非管理员只能操作自己相关的订单
        if (!"admin".equals(ctx.getRole())
                && !order.getBuyerId().equals(ctx.getUserId())
                && !order.getSellerId().equals(ctx.getUserId())) {
            return Result.error(403, "无权限操作该订单");
        }
        orderMapper.updateStatus(id, status);
        // 取消订单时，恢复商品为在售状态
        if (status == 3) {
            productMapper.updateStatus(order.getProductId(), 1);
        }
        operationLogMapper.insert(newLog("订单 id=" + id + " 状态改为 " + status));
        return Result.success();
    }

    private OperationLog newLog(String operation) {
        OperationLog log = new OperationLog();
        AuthContext ctx = AuthContext.get();
        log.setUserId(ctx == null ? null : ctx.getUserId());
        log.setOperation(operation);
        return log;
    }
}
