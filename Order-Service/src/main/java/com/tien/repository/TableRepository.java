package com.tien.repository;

import com.tien.model.RestaurantTable;
import com.tien.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByStatus(TableStatus status);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 
        FROM restaurant_db.restaurant_tables 
        WHERE table_number = :tableNumber
    ) AS table_exists
    """, nativeQuery = true)
    Integer existsByTableNumber(@Param("tableNumber") Integer tableNumber);
}
