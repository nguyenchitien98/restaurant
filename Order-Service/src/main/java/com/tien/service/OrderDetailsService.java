package com.tien.service;

import com.tien.model.OrderDetail;
import com.tien.model.OrderDetailStatus;
import com.tien.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailsService {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }


    public List<OrderDetail> getOrderDetailsByStatus(OrderDetailStatus status) {
        return orderDetailRepository.findByStatus(status);
    }

    public OrderDetail updateOrderDetailStatus(Long id, OrderDetailStatus newStatus) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found with ID: " + id));
        orderDetail.setStatus(newStatus);
        return orderDetailRepository.save(orderDetail);
    }
}
