package com.tien.controller;

import com.tien.dto.request.TableRequest;
import com.tien.model.RestaurantTable;
import com.tien.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/create")
    public ResponseEntity<RestaurantTable> createTable(@RequestBody TableRequest tableRequest) {
        RestaurantTable restaurantTable = tableService.createTable(tableRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantTable);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        List<RestaurantTable> restaurantTable = tableService.getAllTables();
        return ResponseEntity.ok(restaurantTable);
    }
}
