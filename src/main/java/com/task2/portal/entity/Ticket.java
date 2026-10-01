package com.task2.portal.entity;
import jakarta.persistence.*;

import java.time.Instant;

import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 200)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Ticket() {
    }

    public Ticket(Customer customer, String subject, TicketStatus status, Priority priority) {
        this.customer = customer;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getSubject() { return subject; }
    public TicketStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void update(String subject, TicketStatus status, Priority priority) {
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.updatedAt = Instant.now();
    }
}
