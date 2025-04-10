CREATE TABLE restaurant_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_number INT NOT NULL UNIQUE,
    capacity INT NOT NULL,
    status ENUM('OCCUPIED', 'RESERVED', 'EMPTY') DEFAULT 'EMPTY'
);