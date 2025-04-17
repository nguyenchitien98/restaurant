package com.tien.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderDetailId;
    private Long orderId;
    private Long menuId;
    private String menuName;
    private Integer tableNumber;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}
