package com.task2.portal.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import com.task2.portal.enums.OrderStatus;
import com.task2.portal.enums.Priority;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "item", nullable = false)
    private List<String> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(Customer customer, List<String> items, OrderStatus status, Priority priority, BigDecimal total) {
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.priority = priority;
        this.total = total;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<String> getItems() { return List.copyOf(items); }
    public OrderStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }

    public void update(List<String> items, OrderStatus status, Priority priority, BigDecimal total) {
        this.items = new ArrayList<>(items);
        this.status = status;
        this.priority = priority;
        this.total = total;
    }
}
