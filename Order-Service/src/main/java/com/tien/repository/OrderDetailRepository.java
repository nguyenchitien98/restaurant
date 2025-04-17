package com.tien.repository;

import com.tien.model.OrderDetail;
import com.tien.model.OrderDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder_OrderId(Long orderId);

    List<OrderDetail> findByStatus(OrderDetailStatus status);

    @Query(value = "SELECT * FROM restaurant_db.order_details " +
            "WHERE DATE(created_at) = CURDATE() " +
            "AND status = :status " +
            "ORDER BY created_at ASC", nativeQuery = true)
    List<OrderDetail> findTodayOrderDetailsWithStatusNative(@Param("status") String status);
}
