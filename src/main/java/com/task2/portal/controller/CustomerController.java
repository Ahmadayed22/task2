package com.task2.portal.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.task2.portal.dto.CustomerRequest;
import com.task2.portal.dto.CustomerResponse;
import com.task2.portal.service.CustomerService;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/customers")
public class CustomerController {
        private final CustomerService customerService;

        public CustomerController(CustomerService customerService) {
            this.customerService = customerService;
        }

        @GetMapping
        public ResponseEntity<List<CustomerResponse>> getAllCustomer() {
            return ResponseEntity.ok(customerService.findAll());
        }
        
    
        @GetMapping("/{customerId}")
        public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId) {
            return ResponseEntity.ok(customerService.findById(customerId));
        }

        @PostMapping 
        public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
            CustomerResponse created = customerService.create(request);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.id())
                    .toUri();
            return ResponseEntity.created(location).body(created);
        }
        
        @PutMapping("/{customerId}")
        public ResponseEntity<CustomerResponse> replace(@PathVariable Long customerId,
                @RequestBody CustomerRequest request) {

            return ResponseEntity.ok(customerService.replace(customerId, request));
        }
        
        @DeleteMapping("/{customerId}") 
        public ResponseEntity<Void> delete(@PathVariable Long customerId) {
            customerService.delete(customerId);

            return ResponseEntity.noContent().build();
        }

    
}
