package com.tien.repository;

import com.tien.model.Order;
import com.tien.model.OrderStatus;
import com.tien.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByStatus(OrderStatus status);

    Optional<Order> findByTableAndStatus(RestaurantTable table, OrderStatus status);
}
