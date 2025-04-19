package com.tien.repository;

import com.tien.model.Invoices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoices, Long> {
    Optional<Invoices> findByOrderId(Long orderId);

    @Query(value = """
        SELECT COALESCE(SUM(total_amount), 0)
        FROM invoices
        WHERE YEARWEEK(created_at, 1) = YEARWEEK(CURDATE(), 1)
    """, nativeQuery = true)
    Double getTotalRevenueWeekly();

    List<Invoices> findByCreatedAtBetween(LocalDate createdAtAfter, LocalDate createdAtBefore);

    @Query(value = """
    SELECT 
        day_of_week,
        CASE day_of_week
            WHEN 1 THEN 'Sunday'
            WHEN 2 THEN 'Monday'
            WHEN 3 THEN 'Tuesday'
            WHEN 4 THEN 'Wednesday'
            WHEN 5 THEN 'Thursday'
            WHEN 6 THEN 'Friday'
            WHEN 7 THEN 'Saturday'
        END AS weekday,
        SUM(total_amount) AS total_revenue
    FROM (
        SELECT 
            DAYOFWEEK(created_at) AS day_of_week,
            total_amount
        FROM invoices
        WHERE YEARWEEK(created_at, 1) = YEARWEEK(CURDATE(), 1)
    ) AS sub
    GROUP BY day_of_week
    ORDER BY day_of_week
""", nativeQuery = true)
    List<Object[]> getWeeklyRevenue();

    @Query("SELECT MONTH(i.createdAt) as month, SUM(i.totalAmount) as totalAmount " +
            "FROM Invoices i " +
            "WHERE YEAR(i.createdAt) = :year " +
            "GROUP BY MONTH(i.createdAt)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    // Tổng doanh thu trong khoảng thời gian bằng JPQL ( JPQL giống với SQL nhưng sử dụng các tên đối tượng Java thay vì tên bảng cơ sở dữ liệu. )
    @Query("SELECT SUM(i.totalAmount) FROM Invoices i WHERE i.createdAt BETWEEN :start AND :end")
    Double sumRevenueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

//     Doanh thu theo ngày bằng navtive queries
    @Query(value = "SELECT DATE(created_at) as date, SUM(total_amount) as total " +
            "FROM restaurant_db.invoices WHERE created_at BETWEEN :start AND :end " +
            "GROUP BY DATE(created_at) ORDER BY DATE(created_at)",nativeQuery = true)
    List<Invoices> getRevenueByDayRange(@Param("start") LocalDate start, @Param("end") LocalDate end);



//     Doanh thu theo ngày bằng navtive queries
    @Query(value = "SELECT date(created_at) AS date, SUM(total_amount) AS totalRevenue\n" +
            "FROM restaurant_db.invoices where date(created_at) like :date \n" +
            "GROUP BY date(created_at)\n" +
            "ORDER BY date;",nativeQuery = true)
    List<Invoices> getRevenueByDay(@Param("date") LocalDate date);

    // Doanh thu theo tháng bằng navtive queries
    @Query(value = "SELECT YEAR(created_at) AS year, month(created_at) as month, SUM(Invoice.totalAmount) as total\n" +
            "            FROM restaurant_db.invoices WHERE created_att BETWEEN :start AND :end \n" +
            "            GROUP BY YEAR(created_at), month(created_at) as month\n" +
            "            ORDER BY year, month", nativeQuery = true)
    List<Invoices> getRevenueByMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);


    // Doanh thu theo năm bằng navtive queries
    @Query(value = "SELECT year(created_at) as year, sum(total_amount) as total\n" +
            "            FROM restaurant_db.invoices WHERE created_at BETWEEN :start AND :end \n" +
            "            GROUP BY year\n" +
            "            ORDER BY year",nativeQuery = true)
    List<Invoices> getRevenueByYear(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
