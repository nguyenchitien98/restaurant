CREATE TABLE kitchen_orders (
                                kitchen_orders_id INT AUTO_INCREMENT PRIMARY KEY,
                                dish VARCHAR(100) NOT NULL,
                                quantity INT NOT NULL,
--                                 order_id INT NOT NULL,
--                                 chef_id INT NOT NULL,
                                status ENUM('PENDING', 'IN_PROGRESS', 'READY', 'SERVED') DEFAULT 'PENDING',
                                started_at TIMESTAMP NULL,
                                completed_at TIMESTAMP NULL
--                                 FOREIGN KEY (order_id) REFERENCES order_service.orders(id),
--                                 FOREIGN KEY (chef_id) REFERENCES user_service.users(id)
);