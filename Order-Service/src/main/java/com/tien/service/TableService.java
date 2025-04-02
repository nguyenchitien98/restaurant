package com.tien.service;

import com.tien.dto.request.TableRequest;
import com.tien.model.RestaurantTable;
import com.tien.model.TableStatus;
import com.tien.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    public RestaurantTable createTable(TableRequest tableRequest) {
        RestaurantTable table = new RestaurantTable();
        table.setCapacity(tableRequest.getCapacity());
        table.setTable_number(tableRequest.getTable_number());
        table.setStatus(TableStatus.valueOf(TableStatus.EMPTY.name())); // Mới tạo bàn sẽ là TRỐNG
        return tableRepository.save(table);
    }

    public RestaurantTable updateTableStatus(Long tableId, TableStatus status) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        table.setStatus(status); // Cập nhật trạng thái bàn
        return tableRepository.save(table);
    }

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }
}
