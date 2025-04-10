package com.tien.controller;

import com.tien.model.Menu;
import com.tien.repository.MenuRepository;
import com.tien.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    private final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    @GetMapping("/category/{category}")
    public List<Menu> getMenusByCategory(@PathVariable String category) {
        return menuService.getMenusByCategory(category);
    }

    @PostMapping
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.updateMenu(id, menu));
    }

    @DeleteMapping("/{id}")
    public String deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return "Menu item deleted successfully!";
    }

    // Upload ảnh
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        LOGGER.info(file.getOriginalFilename());

            String imageUrl = menuService.uploadImage(file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

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
