package com.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.platform.dao.OmsOrderDao;
import com.platform.dao.OmsOrderOperateHistoryDao;
import com.platform.dto.*;
import com.platform.mapper.OmsOrderMapper;
import com.platform.mapper.OmsOrderOperateHistoryMapper;
import com.platform.model.OmsOrder;
import com.platform.model.OmsOrderExample;
import com.platform.model.OmsOrderOperateHistory;
import com.platform.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by wulinhao on 2019/9/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货
        int count = orderDao.delivery(deliveryParamList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:" + note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息：" + note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public Double getTotalSalesOfToday() {
        //今日销售总额
        Double i = orderMapper.getTotalSalesOfToday();
        i = i == null ? 0.00 : i;
        return i;
    }

    @Override
    public Double getTotalSalesOfYestoday() {
        //昨日销售总额
        Double i = orderMapper.getTotalSalesOfYesToday();
        i = i == null ? 0.00 : i;
        return i;
    }

    @Override
    public Double getTotalSalesOfNearly7Days() {
        //最近7日销售总额
        Double i = orderMapper.getTotalSalesOfNearly7Days();
        i = i == null ? 0.00 : i;
        return i;
    }

    @Override
    public Double getTotalSalesOfWeek() {
        //当周销售总额
        Double i = orderMapper.getTotalSalesOfWeek();
        i = i == null ? 0.00 : i;
        return i;
    }

    @Override
    public Double getTotalSalesOfMonth() {
        //当月销售总额
        Double i = orderMapper.getTotalSalesOfMonth();
        i = i == null ? 0.00 : i;
        return i;
    }

    @Override
    public Integer getNumOfWaitForPay() {
        Integer i = orderMapper.getNumOfWaitForPay();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfFinished() {
        Integer i = orderMapper.getNumOfFinished();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfWaitForConfirmRecvice() {
        Integer i = orderMapper.getNumOfWaitForConfirmRecvice();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfWaitForDeliverGoods() {
        Integer i = orderMapper.getNumOfWaitForDeliverGoods();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfNewShortageRegistration() {
        Integer i = orderMapper.getNumOfNewShortageRegistration();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfWaitForRefundApplication() {
        Integer i = orderMapper.getNumOfWaitForRefundApplication();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getNumOfOutgoingOrders() {
        Integer i = orderMapper.getNumOfOutgoingOrders();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getReturnOrdersToBeProcessed() {
        Integer i = orderMapper.getReturnOrdersToBeProcessed();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getAdvertisingSpaceNealyExpire() {
        Integer i = orderMapper.getAdvertisingSpaceNealyExpire();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getTodayTotalNumOfOrder() {
        Integer i = orderMapper.getTodayTotalNumOfOrder();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getMonthTotalNumOfOrder() {
        Integer i = orderMapper.getMonthTotalNumOfOrder();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getWeekTotalNumOfOrder() {
        Integer i = orderMapper.getWeekTotalNumOfOrder();
        i = i == null ? 0 : i;
        return i;
    }

    @Override
    public Integer getOffShelfGoods() {
        return null;
    }

    @Override
    public Integer getOnShelfGoods() {
        return null;
    }

    @Override
    public Integer getTightStockGoods() {
        return null;
    }

    @Override
    public Integer getAllGoods() {
        return null;
    }

    @Override
    public Integer getAddToday() {
        return null;
    }

    @Override
    public Integer getAddYestoday() {
        return null;
    }

    @Override
    public Integer getAddMonth() {
        return null;
    }

    @Override
    public Integer getAllMembers() {
        return null;
    }
}