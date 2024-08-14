package com.logistics.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.RouteCalculator;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteCalculator routeCalculator;

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateRoute(@RequestBody RouteRequest request) {
        Map<String, Object> result = routeCalculator.calculateRoute(
            request.getOrigin(),
            request.getDestination(),
            request.getDepartureTime()
        );
        return ResponseEntity.ok(result);
    }

    static class RouteRequest {
        private String origin;
        private String destination;
        private String departureTime;

        
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public String getDepartureTime() { return departureTime; }
        public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    }
}