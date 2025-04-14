package com.tien.dto.request;


import com.tien.model.OrderDetailStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private OrderDetailStatus status;
}
