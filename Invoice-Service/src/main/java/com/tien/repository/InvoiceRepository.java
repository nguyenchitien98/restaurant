package com.tien.repository;

import com.tien.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByOrderId(Long orderId);

    List<Invoice> findByCreatedAtBetween(LocalDate createdAtAfter, LocalDate createdAtBefore);

    // Tổng doanh thu trong khoảng thời gian bằng JPQL ( JPQL giống với SQL nhưng sử dụng các tên đối tượng Java thay vì tên bảng cơ sở dữ liệu. )
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Double sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Doanh thu theo ngày bằng navtive queries
    @Query(value = "SELECT DATE(i.createdAt) as date, SUM(i.totalAmount) as total " +
            "FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end " +
            "GROUP BY DATE(i.createdAt) ORDER BY DATE(i.createdAt)",nativeQuery = true)
    List<Object[]> getDailyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Doanh thu theo tháng bằng navtive queries
    @Query(value = "SELECT FUNCTION('YEAR', i.createdAt) as year, FUNCTION('MONTH', i.createdAt) as month, SUM(i.totalAmount) as total " +
            "FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('YEAR', i.createdAt), FUNCTION('MONTH', i.createdAt) " +
            "ORDER BY year, month", nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Doanh thu theo năm bằng navtive queries
    @Query(value = "SELECT FUNCTION('YEAR', i.createdAt) as year, SUM(i.totalAmount) as total " +
            "FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('YEAR', i.createdAt) ORDER BY year",nativeQuery = true)
    List<Object[]> getYearlyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
