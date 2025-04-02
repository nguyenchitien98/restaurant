package com.tien.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableRequest {
    private Integer table_number; // Tên bàn (ví dụ: Bàn 1, Bàn 2, ...)
    private Integer capacity;
    private String status;
}
