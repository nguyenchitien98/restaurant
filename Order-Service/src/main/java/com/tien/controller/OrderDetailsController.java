package com.tien.controller;

import com.tien.dto.request.UpdateStatusRequest;
import com.tien.model.OrderDetail;
import com.tien.model.OrderDetailStatus;
import com.tien.service.OrderDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderDetailsService orderDetailsService;

    @PostMapping
    public OrderDetail createOrderDetail(@RequestBody OrderDetail orderDetail) {
        return orderDetailsService.createOrderDetail(orderDetail);
    }

    // GET danh sách theo status
    @GetMapping
    public ResponseEntity<List<OrderDetail>> getByStatus(@RequestParam("status") OrderDetailStatus status) {
        return ResponseEntity.ok(orderDetailsService.getOrderDetailsByStatus(status));
    }

    // PUT cập nhật status
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetail> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(orderDetailsService.updateOrderDetailStatus(id, request.getStatus()));
    }
}
