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
    Double sumRevenueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

//     Doanh thu theo ngày bằng navtive queries
    @Query(value = "SELECT DATE(created_at) as date, SUM(total_amount) as total " +
            "FROM restaurant_db.invoices WHERE created_at BETWEEN :start AND :end " +
            "GROUP BY DATE(created_at) ORDER BY DATE(created_at)",nativeQuery = true)
    List<Invoice> getRevenueByDayRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

//     Doanh thu theo ngày bằng navtive queries
    @Query(value = "SELECT date(created_at) AS date, SUM(total_amount) AS totalRevenue\n" +
            "FROM restaurant_db.invoices where date(created_at) like :date \n" +
            "GROUP BY date(created_at)\n" +
            "ORDER BY date;",nativeQuery = true)
    List<Invoice> getRevenueByDay(@Param("date") LocalDate date);

    // Doanh thu theo tháng bằng navtive queries
    @Query(value = "SELECT YEAR(created_at) AS year, month(created_at) as month, SUM(Invoice.totalAmount) as total\n" +
            "            FROM restaurant_db.invoices WHERE created_att BETWEEN :start AND :end \n" +
            "            GROUP BY YEAR(created_at), month(created_at) as month\n" +
            "            ORDER BY year, month", nativeQuery = true)
    List<Invoice> getRevenueByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);


    // Doanh thu theo năm bằng navtive queries
    @Query(value = "SELECT year(created_at) as year, sum(total_amount) as total\n" +
            "            FROM restaurant_db.invoices WHERE created_at BETWEEN :start AND :end \n" +
            "            GROUP BY year\n" +
            "            ORDER BY year",nativeQuery = true)
    List<Invoice> getRevenueByYear(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
