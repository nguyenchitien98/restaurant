package com.tien.dto.request;

import com.tien.model.TableStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableStatusRequest {
    private Long tableId; // Tên bàn (ví dụ: Bàn 1, Bàn 2, ...)
    private TableStatus status;
}
