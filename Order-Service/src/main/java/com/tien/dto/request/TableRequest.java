package com.tien.dto.request;

import com.tien.model.TableStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TableRequest {
    private Integer table_number; // Tên bàn (ví dụ: Bàn 1, Bàn 2, ...)
    private Integer capacity;
    private TableStatus status;
    private String note;
    private LocalDateTime reserved_at;
}
