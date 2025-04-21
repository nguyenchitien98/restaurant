package com.tien.controller;

import com.tien.model.Menu;
import com.tien.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Quản Lý Món Ăn")
public class MenuController {

    private final MenuService menuService;

    private final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    @Operation(summary = "Lấy tất cả món ăn", description = "Lấy tất cả thông tin món ăn")
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }

    @Operation(summary = "Lấy món ăn theo id", description = "Lấy thông tin món ăn theo id")
    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    @Operation(summary = "Lấy món theo loại", description = "Lấy thông tin món ăn theo loại món ăn")
    @GetMapping("/category/{category}")
    public List<Menu> getMenusByCategory(@PathVariable String category) {
        return menuService.getMenusByCategory(category);
    }

    @Operation(summary = "Tạo món ăn", description = "Tạo mới một món ăn")
    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    @Operation(summary = "Cập nhật món ăn", description = "Cập nhật một món ăn")
    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.updateMenu(id, menu));
    }

    @Operation(summary = "Xóa món ăn", description = "Xóa một món ăn")
    @DeleteMapping("/{id}")
    public String deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return "Menu item deleted successfully!";
    }

    // Upload ảnh
    @Operation(summary = "Up ảnh món ăn", description = "Upload một ảnh cho món ăn")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        LOGGER.info(file.getOriginalFilename());

            String imageUrl = menuService.uploadImage(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

    }

    @Operation(summary = "Lấy tên món ăn theo id", description = "Lấy danh sách tên món ăn theo id")
    @PostMapping("/names-by-ids")
    public ResponseEntity<Map<Long, String>> getMenuNames(@RequestBody List<Long> menuIds) {
        Map<Long, String> result = menuService.getMenuNamesByIds(menuIds);
        return ResponseEntity.ok(result);
    }


//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
//        try {
//            // đường dẫn tương đối "uploads/"
//            String uploadDir = "uploads/";
//            File folder = new File(uploadDir);
//            if (!folder.exists()) folder.mkdirs();
//
//            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            File dest = new File(uploadDir + filename);
//
//            System.out.println("Save to: " + dest.getAbsolutePath());
//            file.transferTo(dest);
//            return ResponseEntity.ok(Map.of("imageUrl", "/uploads/" + filename));
//        } catch (IOException e) {
//            e.printStackTrace(); // 🔥 Xem lỗi thật sự ở đây
//            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
//        }
//    }

}
