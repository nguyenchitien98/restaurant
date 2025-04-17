package com.tien.client;

import com.tien.model.Menu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "Menu-Service", url = "http://localhost:8082")
public interface MenuClient {
    @GetMapping("/api/menus/{id}")
    Menu getMenuById(@PathVariable Long id);

    @PostMapping("/api/menus/names-by-ids")
    Map<Long, String> getMenuNames(@RequestBody List<Long> menuIds);
}