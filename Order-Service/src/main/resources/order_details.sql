CREATE TABLE order_details (
                               order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
                               order_id INT NOT NULL,
                               menu_id INT NOT NULL,
                               quantity INT NOT NULL,
                               price DECIMAL(10,2) NOT NULL,
                               FOREIGN KEY (order_id) REFERENCES orders(order_id)
#                                FOREIGN KEY (menu_id) REFERENCES menus(menu_id)
);