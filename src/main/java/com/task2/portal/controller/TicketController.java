package com.task2.portal.controller;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.task2.portal.dto.PageResponse;
import com.task2.portal.dto.TicketRequest;
import com.task2.portal.dto.TicketResponse;
import com.task2.portal.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers/{customerId}/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TicketResponse>> findAll(
            @PathVariable Long customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ticketService.findCustomerTickets(customerId, status, priority, page, size));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> findById(@PathVariable Long customerId, @PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.findById(customerId, ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @PathVariable Long customerId,
            @Valid @RequestBody TicketRequest request) {
        TicketResponse created = ticketService.create(customerId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> replace(
            @PathVariable Long customerId,
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.update(customerId, ticketId, request));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> delete(@PathVariable Long customerId, @PathVariable Long ticketId) {
        ticketService.delete(customerId, ticketId);
        return ResponseEntity.noContent().build();
    }
}