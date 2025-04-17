//package com.tien.service.kafka;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class MenuCache {
//    private final Map<Long, String> menuIdNameMap = new ConcurrentHashMap<>();
//
//    public Map<Long, String> getMap() {
//        return menuIdNameMap;
//    }
//
//    public void update(Map<Long, String> newMap) {
//        menuIdNameMap.clear();
//        menuIdNameMap.putAll(newMap);
//    }
//}
