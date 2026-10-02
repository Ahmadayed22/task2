package com.task2.portal.controller;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.task2.portal.dto.OrderRequest;
import com.task2.portal.dto.OrderResponse;
import com.task2.portal.dto.PageResponse;
import com.task2.portal.service.OrderService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/customers/{customerId}/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrder(
            @PathVariable Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(orderService.findCustomerOrders(customerId, status, sort, page, size, priority));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findById(customerId, orderId));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @PathVariable Long customerId,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse created = orderService.create(customerId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> replace(
            @PathVariable Long customerId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.update(customerId, orderId, request));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId, @PathVariable Long orderId) {
        orderService.delete(customerId, orderId);
        return ResponseEntity.noContent().build();
    }
}
