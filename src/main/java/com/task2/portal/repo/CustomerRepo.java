package com.task2.portal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task2.portal.entity.Customer;

public interface CustomerRepo extends  JpaRepository<Customer, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
