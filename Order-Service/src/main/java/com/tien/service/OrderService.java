package com.tien.service;

import com.tien.client.MenuClient;
import com.tien.dto.event.OrderDetailEvent;
import com.tien.dto.event.OrderEvent;
import com.tien.dto.event.OrderEventStatus;
import com.tien.dto.request.OrderRequest;
import com.tien.dto.request.TableStatusRequest;
import com.tien.dto.response.OrderDetailResponse;
import com.tien.dto.response.OrderItemDTO;
import com.tien.dto.response.OrderResponseDTO;
import com.tien.service.kafka.OrderKafkaProducer;
import com.tien.model.*;
import com.tien.repository.OrderDetailRepository;
import com.tien.repository.OrderRepository;
import com.tien.service.kafka.OrderStatusProducer;
import com.tien.websocket.OrderWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderDetailRepository orderDetailRepository;
    private final TableService tableService;
    private final MenuClient menuClient;
    private final OrderWebSocketService orderWebSocketService;
    private final OrderKafkaProducer orderKafkaProducer;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderStatusProducer orderStatusProducer;


    // Tạo mới order cho bàn
    public Order createOrder(Long tableId, OrderRequest orderRequest) {
        Order order = new Order();
        RestaurantTable table = tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
        for (OrderDetail orderDetail : orderRequest.getOrderDetails()) {
            Menu item = menuClient.getMenuById(orderDetail.getMenuId());
            if (item == null){
                throw new RuntimeException("Menu item not found: " + orderDetail.getMenuId());
            }
            orderDetail.setOrder(order);
            orderDetail.setStatus(OrderDetailStatus.PENDING);
            orderDetail.setCreatedAt(LocalDateTime.now());
        }

        order.setTable(table);
        order.setStatus(OrderStatus.PENDING);
        order.setUserId(orderRequest.getUserId());
        order.setOrderDetails(orderRequest.getOrderDetails());
        order.setTotalPrice(orderRequest.getOrderDetails().stream().mapToDouble(od -> od.getPrice() * od.getQuantity()).sum());

        Order savedOrder = orderRepository.save(order);

        // Chuyển OrderDetail thành danh sách sự kiện
        List<OrderDetailEvent> detailEvents = savedOrder.getOrderDetails().stream()
                .map(d -> new OrderDetailEvent(d.getOrderDetailId(), menuClient.getMenuById(d.getMenuId()).getName(), d.getQuantity()))
                .collect(Collectors.toList());

        // Gửi sự kiện tới Kitchen Service
        OrderEvent event = new OrderEvent(savedOrder.getOrderId(), detailEvents);
        kafkaTemplate.send("kitchen-orders", event);

        // Gửi WebSocket tới UI kitchen
        Integer tableNumber = table.getTable_number();
        List<OrderDetailResponse> kitchenItems = savedOrder.getOrderDetails().stream()
                .map(detail -> {
                    Menu menu = menuClient.getMenuById(detail.getMenuId());
                    return new OrderDetailResponse(
                            detail.getOrderDetailId(),
                            savedOrder.getOrderId(),
                            detail.getMenuId(),
                            menu != null ? menu.getName() : "Unknown",
                            tableNumber,
                            detail.getQuantity(),
                            detail.getStatus().name(),
                            LocalDateTime.now()
                    );
                })
                .collect(Collectors.toList());

        for (OrderDetailResponse item : kitchenItems) {
            System.out.println(item);
            messagingTemplate.convertAndSend("/topic/kitchen", item);
        }

        return savedOrder;
    }

    public Order createOrUpdateOrder(Long tableId,Long userId, List<OrderDetail> orderDetailList, String note) {
        // 1. Kiểm tra xem bàn đã có order chưa thanh toán chưa
        RestaurantTable table = tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
        Optional<Order> existingOrderOpt = orderRepository.findByTableAndStatus(table,OrderStatus.PENDING);

        if (existingOrderOpt.isPresent()) {
            Order existingOrder = existingOrderOpt.get();
            List<OrderDetail> newlyAddedItems = new ArrayList<>();

            // 2. Gộp món mới vào đơn cũ
            for (OrderDetail itemsOrderDetail : orderDetailList) {
                Optional<OrderDetail> existingOrderDetail = existingOrder.getOrderDetails().stream()
                        .filter(i -> i.getMenuId().equals(itemsOrderDetail.getMenuId()))
                        .findFirst();

                if (existingOrderDetail.isPresent()) {
                    OrderDetail item = existingOrderDetail.get();
                    item.setQuantity(item.getQuantity() + itemsOrderDetail.getQuantity());
                } else {
                    OrderDetail newItem = new OrderDetail();
                    newItem.setMenuId(itemsOrderDetail.getMenuId());
                    newItem.setQuantity(itemsOrderDetail.getQuantity());
                    newItem.setPrice(itemsOrderDetail.getPrice());
                    newItem.setNote(note);
                    newItem.setStatus(OrderDetailStatus.PENDING);
                    newItem.setCreatedAt(LocalDateTime.now());
                    newItem.setOrder(existingOrder);
                    existingOrder.getOrderDetails().add(newItem);
                    newlyAddedItems.add(newItem); // track món mới
                }
            }
            // Đặt lại tổng tiền sau khi đã xử lý tất cả món
            double total = existingOrder.getOrderDetails().stream()
                    .mapToDouble(od -> od.getPrice() * od.getQuantity())
                    .sum();
            existingOrder.setTotalPrice(total);

            if (note != null && !note.isEmpty()) {
                existingOrder.setNote(existingOrder.getNote() + " | " + note);
            }

            Order updatedOrder = orderRepository.save(existingOrder);

            // Gửi WebSocket cho từng món mới
            for (OrderDetail newItem : newlyAddedItems) {
                Menu menu = menuClient.getMenuById(newItem.getMenuId());

                OrderDetailResponse response = new OrderDetailResponse();
                response.setOrderDetailId(newItem.getOrderDetailId());
                response.setOrderId(updatedOrder.getOrderId());
                response.setMenuId(newItem.getMenuId());
                response.setMenuName(menu.getName());
                response.setQuantity(newItem.getQuantity());
                response.setTableNumber(table.getTable_number());
                response.setStatus(newItem.getStatus().toString());
                response.setCreatedAt(newItem.getCreatedAt());

                messagingTemplate.convertAndSend("/topic/kitchen", response);
            }

            return updatedOrder;
        } else {
            // 3. Tạo đơn mới
            Order newOrder = new Order();
            newOrder.setTable(table);
            newOrder.setUserId(userId);
            newOrder.setStatus(OrderStatus.PENDING);
            newOrder.setNote(note);
            newOrder.setCreatedAt(LocalDate.now());

            List<OrderDetail> itemEntities = orderDetailList.stream().map(dto -> {
                OrderDetail item = new OrderDetail();
                item.setMenuId(dto.getMenuId());
                item.setQuantity(dto.getQuantity());
                item.setPrice(dto.getPrice());
                item.setStatus(OrderDetailStatus.PENDING);
                item.setCreatedAt(LocalDateTime.now());
                item.setOrder(newOrder);
                return item;
            }).collect(Collectors.toList());
            newOrder.setOrderDetails(itemEntities);
            newOrder.setTotalPrice(orderDetailList.stream().mapToDouble(od -> od.getPrice() * od.getQuantity()).sum());

            Order savedOrder = orderRepository.save(newOrder);

            // Gửi WebSocket cho tất cả món
            for (OrderDetail od : savedOrder.getOrderDetails()) {
                Menu menu = menuClient.getMenuById(od.getMenuId());

                OrderDetailResponse response = new OrderDetailResponse();
                response.setOrderDetailId(od.getOrderDetailId());
                response.setOrderId(savedOrder.getOrderId());
                response.setMenuId(od.getMenuId());
                response.setMenuName(menu.getName());
                response.setQuantity(od.getQuantity());
                response.setTableNumber(table.getTable_number());
                response.setStatus(od.getStatus().toString());
                response.setCreatedAt(od.getCreatedAt());

                messagingTemplate.convertAndSend("/topic/kitchen", response);
            }

            return savedOrder;
        }
    }

    public Order updateOrder(Long tableId,Long userId, List<OrderDetail> orderDetailList, String note) {
        // 1. Kiểm tra xem bàn đã có order chưa thanh toán chưa
        RestaurantTable table = tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
        Order existingOrder = orderRepository.findByTableAndStatus(table,OrderStatus.PENDING).orElseThrow(() -> new RuntimeException("Order not found"));

            List<OrderDetail> newlyAddedItems = new ArrayList<>();

            // 2. Gộp món mới vào đơn cũ
            for (OrderDetail itemsOrderDetail : orderDetailList) {
                Optional<OrderDetail> existingOrderDetail = existingOrder.getOrderDetails().stream()
                        .filter(i -> i.getMenuId().equals(itemsOrderDetail.getMenuId()))
                        .findFirst();

                if (existingOrderDetail.isPresent()) {
                    OrderDetail item = existingOrderDetail.get();
                    item.setQuantity(item.getQuantity() + itemsOrderDetail.getQuantity());
                } else {
                    OrderDetail newItem = new OrderDetail();
                    newItem.setMenuId(itemsOrderDetail.getMenuId());
                    newItem.setQuantity(itemsOrderDetail.getQuantity());
                    newItem.setPrice(itemsOrderDetail.getPrice());
                    newItem.setNote(note);
                    newItem.setStatus(OrderDetailStatus.PENDING);
                    newItem.setCreatedAt(LocalDateTime.now());
                    newItem.setOrder(existingOrder);
                    existingOrder.getOrderDetails().add(newItem);
                    newlyAddedItems.add(newItem); // track món mới
                }
            }
            // Đặt lại tổng tiền sau khi đã xử lý tất cả món
            double total = existingOrder.getOrderDetails().stream()
                    .mapToDouble(od -> od.getPrice() * od.getQuantity())
                    .sum();
            existingOrder.setTotalPrice(total);
            existingOrder.setUserId(userId);

            if (note != null && !note.isEmpty()) {
//                existingOrder.setNote(existingOrder.getNote() + " | " + note);
                existingOrder.setNote(note);
            }

            Order updatedOrder = orderRepository.save(existingOrder);

            // Gửi WebSocket cho từng món mới
            for (OrderDetail newItem : newlyAddedItems) {
                Menu menu = menuClient.getMenuById(newItem.getMenuId());

                OrderDetailResponse response = new OrderDetailResponse();
                response.setOrderDetailId(newItem.getOrderDetailId());
                response.setOrderId(updatedOrder.getOrderId());
                response.setMenuId(newItem.getMenuId());
                response.setMenuName(menu.getName());
                response.setQuantity(newItem.getQuantity());
                response.setTableNumber(table.getTable_number());
                response.setStatus(newItem.getStatus().toString());
                response.setCreatedAt(newItem.getCreatedAt());

                messagingTemplate.convertAndSend("/topic/kitchen", response);
            }

            return updatedOrder;
    }


    public void completeOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));

        orderDetail.setStatus(OrderDetailStatus.COMPLETED);
        orderDetailRepository.save(orderDetail);

        // Kiểm tra nếu tất cả món đã hoàn thành thì cập nhật Order
        Long orderId = orderDetail.getOrder().getOrderId();
        boolean allCompleted = orderDetailRepository
                .findByOrder_OrderId(orderId)
                .stream()
                .allMatch(d -> d.getStatus().equals(OrderDetailStatus.COMPLETED));

        if (allCompleted) {
            Order order = orderRepository.findById(orderId).orElseThrow();
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            OrderEventStatus orderEventStatus = new OrderEventStatus();
            orderEventStatus.setOrderId(orderId);
            orderEventStatus.setStatus(OrderStatus.COMPLETED);
            orderEventStatus.setTotalAmount(order.getTotalPrice());
            orderStatusProducer.sendOrderStatusToInvoice(orderEventStatus);
        }
    }



    // Lấy chi tiết order của bàn
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    // Cập nhật trạng thái bàn sau khi gọi món
    public RestaurantTable updateTableStatusAfterOrder(Long tableId) {
        TableStatusRequest tableStatusRequest = new TableStatusRequest(tableId,TableStatus.OCCUPIED);
        tableService.updateTableStatus(tableStatusRequest);
        return tableService.getAllTables().stream()
                .filter(t -> t.getTable_id().equals(tableId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setOrderId(order.getOrderId());
            dto.setTableId(order.getTable().getTable_id());
            dto.setTableNumber(order.getTable().getTable_number());
            dto.setNote(order.getNote());
            dto.setStatus(order.getStatus());
            dto.setCreatedAt(order.getCreatedAt());

            List<OrderItemDTO> items = order.getOrderDetails().stream().map(detail -> {
                Menu menu = menuClient.getMenuById(detail.getMenuId());
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setMenuName(menu.getName());
                itemDTO.setQuantity(detail.getQuantity());
                itemDTO.setPrice(menu.getPrice());
                return itemDTO;
            }).collect(Collectors.toList());

            dto.setItems(items);
            return dto;
        }).collect(Collectors.toList());
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);

            // Gửi sự kiện WebSocket
            orderWebSocketService.sendOrderUpdate(updatedOrder);

            // Gửi sự kiện Kafka đến Kitchen Service
//            orderKafkaProducer.sendOrderStatusUpdate(updatedOrder);

            return updatedOrder;
    }

    public void deleteOrder(Long orderId, Long tableId) {
        tableService.updateStatusTable(tableId);
        orderRepository.deleteById(orderId);
    }

}
