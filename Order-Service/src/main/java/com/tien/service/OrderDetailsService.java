package com.tien.service;

import com.tien.client.MenuClient;
//import com.tien.dto.event.MenuIdRequestEvent;
import com.tien.dto.response.OrderDetailResponse;
import com.tien.model.Order;
import com.tien.model.OrderDetail;
import com.tien.model.OrderDetailStatus;
import com.tien.model.RestaurantTable;
import com.tien.repository.OrderDetailRepository;
//import com.tien.service.kafka.MenuCache;
//import com.tien.service.kafka.MenuNameKafkaConsumer;
import com.tien.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailsService {
    private final OrderDetailRepository orderDetailRepository;
//    private final MenuCache menuCache;
//    private final KafkaTemplate<String, MenuIdRequestEvent> kafkaTemplate;
    private final MenuClient menuClient;
    private final OrderRepository orderRepository; // để lấy Order theo orderId

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

    // Không sử dụng được, để làm kỉ niệm, off cuối cùng là để biểu thị ko dùng
//    public List<OrderDetailResponse> getAllTodayOrderDetailsByStatus_off(OrderDetailStatus status) {
//        List<OrderDetail> orderDetails = orderDetailRepository.findTodayOrderDetailsWithStatusNative(status.name());
//
//        // Lấy danh sách menuId duy nhất
//        Set<Object> menuIds = orderDetails.stream()
//                .map(OrderDetail::getMenuId)
//                .collect(Collectors.toSet());
//
//        // Gửi sang Menu Service để lấy menuName
//        MenuIdRequestEvent menuIdRequestEvent = new MenuIdRequestEvent(menuIds);
//        kafkaTemplate.send("menu-name-request", menuIdRequestEvent);
//
//        return orderDetails.stream()
//                .map(od -> {
//                    Order order = od.getOrder();
//                    RestaurantTable table = order.getTable();
//                    System.out.println("menuId: " + od.getMenuId() + " | type: " + od.getMenuId().getClass());
//
//                    return new OrderDetailResponse(
//                            od.getOrderDetailId(),
//                            order.getOrderId(),
//                            od.getMenuId(),
//                            menuCache.getMap().getOrDefault(od.getMenuId(), "Unknown"),
//                            table.getTable_number(),
//                            od.getQuantity(),
//                            od.getStatus().name(),
//                            od.getCreatedAt()
//                    );
//                }).collect(Collectors.toList());
//    }

    public List<OrderDetailResponse> getAllTodayOrderDetailsByStatus(OrderDetailStatus status) {
        List<OrderDetail> orderDetails = orderDetailRepository.findTodayOrderDetailsWithStatusNative(status.name());

        // Lấy danh sách menuId duy nhất
        Set<Long> menuIds = orderDetails.stream()
                .map(OrderDetail::getMenuId)
                .collect(Collectors.toSet());

        // Gọi Menu Service lấy menuName
        Map<Long, String> menuIdNameMap = menuClient.getMenuNames(new ArrayList<>(menuIds));

        return orderDetails.stream()
                .map(od -> {
                    Order order = od.getOrder();
                    RestaurantTable table = order.getTable();
                    System.out.println("menuId: " + od.getMenuId() + " | type: " + od.getMenuId().getClass());

                    return new OrderDetailResponse(
                            od.getOrderDetailId(),
                            order.getOrderId(),
                            od.getMenuId(),
                            menuIdNameMap.getOrDefault(od.getMenuId(), "Unknown"),
                            table.getTable_number(),
                            od.getQuantity(),
                            od.getStatus().name(),
                            od.getCreatedAt()
                    );
                }).collect(Collectors.toList());
    }

    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    public OrderDetail getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));
    }

    public OrderDetail createOrderDetail(Long orderId, OrderDetail orderDetail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderDetail.setOrder(order);
        orderDetail.setCreatedAt(LocalDateTime.now());
        orderDetail.setUpdatedAt(LocalDateTime.now());
        return orderDetailRepository.save(orderDetail);
    }

    public OrderDetail updateOrderDetail(Long id, OrderDetail updatedDetail) {
        OrderDetail existing = getOrderDetailById(id);
        existing.setMenuId(updatedDetail.getMenuId());
        existing.setQuantity(updatedDetail.getQuantity());
        existing.setPrice(updatedDetail.getPrice());
        existing.setNote(updatedDetail.getNote());
        existing.setStatus(updatedDetail.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return orderDetailRepository.save(existing);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

}
