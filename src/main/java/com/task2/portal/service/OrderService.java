package com.task2.portal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.task2.portal.dto.OrderRequest;
import com.task2.portal.dto.OrderResponse;
import com.task2.portal.dto.PageResponse;
import com.task2.portal.entity.Customer;
import com.task2.portal.entity.OrderEntity;
import com.task2.portal.enums.OrderStatus;
import com.task2.portal.enums.Priority;
import com.task2.portal.exception.BadRequestException;
import com.task2.portal.exception.ForbiddenException;
import com.task2.portal.exception.ResourceNotFoundException;
import com.task2.portal.repo.CustomerRepo;
import com.task2.portal.repo.OrderRepo;
import com.task2.portal.security.SecurityUtils;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.*;
import jakarta.persistence.criteria.Predicate;

@Service 
public class OrderService {
    private final OrderRepo orderRepository;
    private final CustomerRepo customerRepository;
   
    public OrderService(OrderRepo orderRepository, CustomerRepo customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
  
    }
    
    @Transactional (readOnly = true)
    public PageResponse<OrderResponse> findCustomerOrders(Long customerId, String status,
            String sort, int page, int size, String priority) {
        SecurityUtils.requireCustomerAccess(customerId);
        validatePage(page, size);
        OrderStatus orderStatus = parseEnum(status, OrderStatus.class, "status");
        Priority orderPriority = parseEnum(priority, Priority.class, "priority");

        Specification<OrderEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            if (orderStatus != null)
                predicates.add(cb.equal(root.get("status"), orderStatus));
            if (orderPriority != null)
                predicates.add(cb.equal(root.get("priority"), orderPriority));
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<OrderEntity> result = orderRepository.findAll(specification, buildPageable(page, size, sort));
        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long customerId, Long orderId) {
        SecurityUtils.requireCustomerAccess(customerId);
        return toResponse(orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order " + orderId + " was not found for customer " + customerId)));
    }

    @Transactional
    public OrderResponse create(Long customerId, OrderRequest request) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Only ADMIN users can create orders");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId
                 + " was not found"));
        OrderEntity order = new OrderEntity(customer, request.items(), request.status(),
                request.priority(), request.total());
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse update(Long customerId, Long orderId, OrderRequest request) {
        SecurityUtils.requireCustomerAccess(customerId);
        OrderEntity order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " was not found for customer " + customerId));

        OrderStatus previousStatus = order.getStatus();
        order.update(request.items(), request.status(), request.priority(), request.total());
        OrderEntity saved = orderRepository.save(order);


        return toResponse(saved);
    }


    @Transactional
    public void delete(Long customerId, Long orderId) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Only ADMIN users can delete orders");
        }
        OrderEntity order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " was not found for customer " + customerId));
        orderRepository.delete(order);
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String normalized = sort == null ? "newest" : sort.trim().toLowerCase();
        Sort.Direction direction = switch (normalized) {
            case "oldest", "createdat:asc", "createdat,asc" -> Sort.Direction.ASC;
            case "newest", "createdat:desc", "createdat,desc" -> Sort.Direction.DESC;
            default -> throw new BadRequestException("Unsupported sort value. Use newest, oldest, createdAt:desc or createdAt:asc");
        };
        return PageRequest.of(page, size, Sort.by(direction, "createdAt"));
    }

    private void validatePage(int page, int size) {
        if (page < 0)
            throw new com.task2.portal.exception.BadRequestException("page must be zero or greater");
        if (size < 1 || size > 100)
            throw new BadRequestException("size must be between 1 and 100");
    }
    

        private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType, String parameterName) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unsupported " + parameterName + " value: " + value);
        }
    }

    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(order.getId(), order.getCustomer().getId(), order.getItems(), order.getStatus(), order.getPriority(), order.getTotal(), order.getCreatedAt());
    }
}
