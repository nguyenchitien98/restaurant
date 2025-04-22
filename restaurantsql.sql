CREATE TABLE users (
                       user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE restaurant_tables (
    table_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    table_number INT NOT NULL UNIQUE,
    capacity INT NOT NULL,
    note VARCHAR(200),
    reserved_at datetime,
    status ENUM('OCCUPIED', 'RESERVED', 'EMPTY') DEFAULT 'EMPTY'
    -- RESERVED: Đặt trước (để dành)
    -- OCCUPIED: Chiếm lĩnh ( Đang sử dụng )
    -- EMPTY: Trống (Có sẵn )
);

CREATE TABLE orders (
						order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        table_id BIGINT NOT NULL,
                        total_price DECIMAL(10,2),
                        note VARCHAR(100),
#                         status VARCHAR(50) DEFAULT 'PENDING',
                        status ENUM('PENDING', 'COMPLETED', 'CANCELLED', 'PAID') DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
						 FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id)
                     # FOREIGN KEY (user_id) REFERENCES users(user_id)                  
);


CREATE TABLE order_details (
                               order_detail_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               order_id BIGINT NOT NULL,
                               menu_id BIGINT NOT NULL,
                               quantity INT NOT NULL,
                               price DECIMAL(10,2) NOT NULL,
                               note VARCHAR(100),
                               status ENUM('PENDING','COOKING','COMPLETED','CANCELLED') DEFAULT 'PENDING',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
							   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                              
                             #  FOREIGN KEY (menu_id) REFERENCES menus(menu_id)
);

CREATE TABLE menus (
                       menu_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT,
                       image_url VARCHAR(100),
                       price DECIMAL(10,2) NOT NULL,
                       category VARCHAR(100),
                       ingredient VARCHAR(100),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE kitchen_orders (
                                kitchen_orders_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                dish VARCHAR(100) NOT NULL,
                                quantity INT NOT NULL,
                         --        order_id INT NOT NULL,
                         --        chef_id INT NOT NULL, 
                                status ENUM('PENDING','COOKING','COMPLETED','CANCELLED') DEFAULT 'PENDING',
                                started_at TIMESTAMP NULL,
                                completed_at TIMESTAMP NULL
--                                 FOREIGN KEY (order_id) REFERENCES order_service.orders(id),
--                                 FOREIGN KEY (chef_id) REFERENCES user_service.users(id)
);

CREATE TABLE invoices (
                          id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          order_id INT NOT NULL,
                          total_amount DECIMAL(10,2) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--                           payment_method ENUM('CASH', 'CARD', 'ONLINE') NOT NULL,
--                           status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
--                           issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--                           FOREIGN KEY (order_id) REFERENCES order_service.orders(id)
);


use restaurant_db;

SHOW FULL COLUMNS FROM users