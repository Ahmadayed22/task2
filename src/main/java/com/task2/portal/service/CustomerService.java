package com.task2.portal.service;

import java.util.List;

import com.task2.portal.dto.CustomerRequest;
import com.task2.portal.dto.CustomerResponse;
import com.task2.portal.entity.Customer;
import com.task2.portal.exception.ConflictException;
import com.task2.portal.exception.ResourceNotFoundException;
import com.task2.portal.repo.CustomerRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class CustomerService {
    private final CustomerRepo repository;

    public CustomerService(CustomerRepo repository) {
        this.repository = repository;
    }
    
    @Transactional (readOnly = true)
    public List<CustomerResponse> findAll() {
          return repository.findAll().stream().map(this::toResponse).toList();
      }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return toResponse(getCustomer(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("A customer with this email already exists");
        }
        Customer customer = new Customer(request.name().trim(), request.email().trim().toLowerCase(),
                request.phone().trim());
        return toResponse(repository.save(customer));
    }

    
    @Transactional
    public CustomerResponse replace(Long id, CustomerRequest request) {
        Customer customer = getCustomer(id);
        if (repository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ConflictException("A customer with this email already exists");
        }
        customer.update(request.name().trim(), request.email().trim().toLowerCase(), request.phone().trim());
        return toResponse(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getCustomer(id);
        repository.delete(customer);
    }

    private Customer getCustomer(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " was not found"));
    }
    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(), customer.getCreatedAt());
    }
}
