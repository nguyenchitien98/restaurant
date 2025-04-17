package com.tien.service;

import com.tien.controller.MenuController;
import com.tien.model.Menu;
import com.tien.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final Logger LOGGER = LoggerFactory.getLogger(MenuService.class);

    private final MenuRepository menuRepository;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public List<Menu> getMenusByCategory(String category) {
        return menuRepository.findByCategory(category);
    }

    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu updateMenu(Long id, Menu updatedMenu) {
        Menu existing = menuRepository.findById(id).orElseThrow();

        // Nếu ảnh đổi → xóa ảnh cũ
        if (existing.getImageUrl() != null && !existing.getImageUrl().equals(updatedMenu.getImageUrl())) {
            deleteFile(existing.getImageUrl());
        }

        existing.setName(updatedMenu.getName());
        existing.setPrice(updatedMenu.getPrice());
        existing.setCategory(updatedMenu.getCategory());
        existing.setDescription(updatedMenu.getDescription());
        existing.setIngredient(updatedMenu.getIngredient());
        existing.setImageUrl(updatedMenu.getImageUrl());

        return menuRepository.save(existing);
    }

//    public String uploadImage(MultipartFile file) throws IOException {
//        File folder = new File(uploadDir);
//        System.out.println("Upload dir: " + uploadDir);
//        if (!folder.exists()) folder.mkdirs();
//        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        File dest = new File(uploadDir + filename);
////        boolean ok = dest.createNewFile();
////        System.out.println("Test file created? " + ok);
//        System.out.println("Save to: " + dest.getAbsolutePath());
//        file.transferTo(dest);
//        LOGGER.info(file.getOriginalFilename());
//        return "/uploads/" + filename;
//    }

public String uploadImage(MultipartFile file){
    try {
        String uploadDir =  System.getProperty("user.dir") + fileUploadDir;
        File folder = new File(uploadDir);
        if (!folder.exists()) folder.mkdirs();
        String filename =file.getOriginalFilename();
        File dest = new File(uploadDir + filename);
        file.transferTo(dest);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        System.out.println(baseUrl);
        LOGGER.info(file.getOriginalFilename());
        return "/uploads/" + filename;
    }catch (IOException e) {
        e.printStackTrace(); // 👈 rất quan trọng
        return "Upload failed: " + e.getMessage();
    }
}

    private void deleteFile(String imageUrl) {
        String uploadDir =  System.getProperty("user.dir") + fileUploadDir;
        String filename = imageUrl.replace("/uploads/", "");
        File file = new File(uploadDir + filename);
        if (file.exists()) file.delete();
    }

    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow();
        if (menu.getImageUrl() != null) {
            deleteFile(menu.getImageUrl());
        }
        menuRepository.delete(menu);
    }

    public Map<Long, String> getMenuNamesByIds(List<Long> ids) {
        List<Menu> menus = menuRepository.findByMenuIdIn(ids);
        return menus.stream()
                .collect(Collectors.toMap(Menu::getMenuId, Menu::getName));
    }
}
