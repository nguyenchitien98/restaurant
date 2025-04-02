CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        table_id INT NOT NULL,
                        total_price DECIMAL(10,2),
#                         status VARCHAR(50) DEFAULT 'PENDING',
                        status ENUM('PENDING', 'CONFIRMED', 'COOKING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
#                         FOREIGN KEY (user_id) REFERENCES users(user_id)
);