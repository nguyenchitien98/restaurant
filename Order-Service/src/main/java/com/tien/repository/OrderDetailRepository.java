package com.tien.repository;

import com.tien.model.OrderDetail;
import com.tien.model.OrderDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder_OrderId(Long orderId);

    List<OrderDetail> findByStatus(OrderDetailStatus status);
}
