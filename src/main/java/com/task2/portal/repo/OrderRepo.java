package com.task2.portal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.task2.portal.entity.OrderEntity;
import com.task2.portal.enums.OrderStatus;

import java.util.List;
import java.util.Optional;
public interface OrderRepo extends  JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByIdAndCustomerId(Long id, Long customerId);
    List<OrderEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<OrderEntity> findByCustomerIdAndStatusOrderByCreatedAtDesc(Long customerId, OrderStatus status);
    
}