package com.tien.dto.request;

import lombok.Data;

@Data
public class TableStatusUpdateRequest {
    private String status; // OCCUPIED, RESERVED, EMPTY
}
