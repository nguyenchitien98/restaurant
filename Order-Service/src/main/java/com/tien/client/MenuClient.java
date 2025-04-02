package com.tien.client;

import com.tien.model.Menu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Menu-Service", url = "http://localhost:8082")
public interface MenuClient {
    @GetMapping("/api/menus/{id}")
    Menu getMenuById(@PathVariable Long id);
}