package com.tien.repository;

import com.tien.model.RestaurantTable;
import com.tien.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByStatus(TableStatus status);
}
