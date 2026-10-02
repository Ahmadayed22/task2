

package com.task2.portal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task2.portal.entity.Ticket;
import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TicketRepo extends JpaRepository<Ticket,Long>{
    Page<Ticket> findByCustomerId(Long customerId, Pageable pageable);
    Page<Ticket> findByCustomerIdAndStatus(Long customerId, TicketStatus status, Pageable pageable);
    Page<Ticket> findByCustomerIdAndPriority(Long customerId, Priority priority, Pageable pageable);
    Page<Ticket> findByCustomerIdAndStatusAndPriority(Long customerId, TicketStatus status, Priority priority, Pageable pageable);
    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);
    
} 