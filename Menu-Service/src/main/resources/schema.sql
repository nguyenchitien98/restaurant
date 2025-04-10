CREATE TABLE menus (
                       menu_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description TEXT,
                       imageUrl VARCHAR(100),
                       price DECIMAL(10,2) NOT NULL,
                       category VARCHAR(100),
                       ingredient VARCHAR(100),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO menus (name, description, imageUrl, price, category, ingredient, created_at, updated_at) VALUES
                                                                                                         ('Phở Bò', 'Phở bò truyền thống với nước dùng đậm đà', 'https://example.com/images/pho_bo.jpg', 45000, 'Món chính', 'Bò, bánh phở, hành, rau thơm', NOW(), NOW()),
                                                                                                         ('Bún Chả', 'Bún chả Hà Nội thơm ngon', 'https://example.com/images/bun_cha.jpg', 40000, 'Món chính', 'Thịt heo nướng, bún, rau sống, nước mắm', NOW(), NOW()),
                                                                                                         ('Cơm Tấm', 'Cơm tấm sườn bì chả', 'https://example.com/images/com_tam.jpg', 50000, 'Món chính', 'Cơm tấm, sườn, bì, chả trứng', NOW(), NOW()),
                                                                                                         ('Gỏi Cuốn', 'Gỏi cuốn tôm thịt chấm nước mắm chua ngọt', 'https://example.com/images/goi_cuon.jpg', 30000, 'Khai vị', 'Tôm, thịt, bánh tráng, rau sống', NOW(), NOW()),
                                                                                                         ('Chả Giò', 'Chả giò chiên giòn vàng ươm', 'https://example.com/images/cha_gio.jpg', 32000, 'Khai vị', 'Thịt, miến, nấm mèo, cà rốt', NOW(), NOW()),
                                                                                                         ('Bánh Xèo', 'Bánh xèo miền Tây với tôm thịt', 'https://example.com/images/banh_xeo.jpg', 35000, 'Món chính', 'Tôm, thịt, bột gạo, giá, rau sống', NOW(), NOW()),
                                                                                                         ('Hủ Tiếu Nam Vang', 'Hủ tiếu với tôm, thịt bằm, trứng cút', 'https://example.com/images/hu_tieu.jpg', 45000, 'Món chính', 'Tôm, thịt bằm, trứng cút, hủ tiếu', NOW(), NOW()),
                                                                                                         ('Trà Đào', 'Trà đào mát lạnh giải nhiệt', 'https://example.com/images/tra_dao.jpg', 25000, 'Đồ uống', 'Trà, đào ngâm, đường', NOW(), NOW()),
                                                                                                         ('Sữa Đậu Nành', 'Sữa đậu nành tươi nguyên chất', 'https://example.com/images/sua_dau_nanh.jpg', 15000, 'Đồ uống', 'Đậu nành, nước, đường', NOW(), NOW()),
                                                                                                         ('Cà Phê Sữa Đá', 'Cà phê Việt Nam đậm vị', 'https://example.com/images/ca_phe_sua.jpg', 30000, 'Đồ uống', 'Cà phê, sữa đặc, đá', NOW(), NOW()),
                                                                                                         ('Nước Chanh', 'Nước chanh tươi mát lạnh', 'https://example.com/images/nuoc_chanh.jpg', 20000, 'Đồ uống', 'Chanh, nước, đường, đá', NOW(), NOW()),
                                                                                                         ('Bánh Flan', 'Bánh flan caramel mềm mịn', 'https://example.com/images/banh_flan.jpg', 20000, 'Tráng miệng', 'Trứng, sữa, đường, caramel', NOW(), NOW()),
                                                                                                         ('Chè Ba Màu', 'Chè đậu đỏ, đậu xanh, thạch và nước cốt dừa', 'https://example.com/images/che_ba_mau.jpg', 22000, 'Tráng miệng', 'Đậu đỏ, đậu xanh, nước cốt dừa, thạch', NOW(), NOW()),
                                                                                                         ('Kem Dừa', 'Kem dừa mát lạnh với topping hấp dẫn', 'https://example.com/images/kem_dua.jpg', 28000, 'Tráng miệng', 'Kem dừa, dừa khô, đậu phộng', NOW(), NOW()),
                                                                                                         ('Xôi Xoài', 'Xôi nếp dẻo thơm ăn cùng xoài chín', 'https://example.com/images/xoi_xoai.jpg', 30000, 'Tráng miệng', 'Nếp, xoài, nước cốt dừa, mè', NOW(), NOW()),
