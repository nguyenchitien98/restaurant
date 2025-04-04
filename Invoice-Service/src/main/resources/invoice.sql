CREATE TABLE invoices (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          order_id INT NOT NULL,
                          total_amount DECIMAL(10,2) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--                           payment_method ENUM('CASH', 'CARD', 'ONLINE') NOT NULL,
--                           status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
--                           issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                           FOREIGN KEY (order_id) REFERENCES order_service.orders(id)
);