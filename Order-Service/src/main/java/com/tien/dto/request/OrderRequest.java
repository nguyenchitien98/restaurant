package com.tien.dto.request;

import com.tien.model.OrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Long tableId;
    private Long userId;
    private List<OrderDetail> orderDetails;
}
