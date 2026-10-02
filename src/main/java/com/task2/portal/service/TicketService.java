package com.task2.portal.service;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task2.portal.dto.PageResponse;
import com.task2.portal.dto.TicketRequest;
import com.task2.portal.dto.TicketResponse;
import com.task2.portal.entity.Customer;
import com.task2.portal.entity.Ticket;
import com.task2.portal.enums.Priority;
import com.task2.portal.enums.TicketStatus;
import com.task2.portal.exception.BadRequestException;
import com.task2.portal.exception.ForbiddenException;
import com.task2.portal.exception.ResourceNotFoundException;
import com.task2.portal.repo.CustomerRepo;
import com.task2.portal.repo.TicketRepo;
import com.task2.portal.security.SecurityUtils;
@Service
public class TicketService {
    private final TicketRepo ticketRepository;
    private final CustomerRepo customerRepository;

    public TicketService(TicketRepo ticketRepository, CustomerRepo customerRepository) {
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketResponse> findCustomerTickets(Long customerId, String status, String priority, int page, int size) {
        SecurityUtils.requireCustomerAccess(customerId);
        validatePage(page, size);
        TicketStatus ticketStatus = parseEnum(status, TicketStatus.class, "status");
        Priority ticketPriority = parseEnum(priority, Priority.class, "priority");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Ticket> result;
        if (ticketStatus != null && ticketPriority != null) {
            result = ticketRepository.findByCustomerIdAndStatusAndPriority(customerId, ticketStatus, ticketPriority, pageable);
        } else if (ticketStatus != null) {
            result = ticketRepository.findByCustomerIdAndStatus(customerId, ticketStatus, pageable);
        } else if (ticketPriority != null) {
            result = ticketRepository.findByCustomerIdAndPriority(customerId, ticketPriority, pageable);
        } else {
            result = ticketRepository.findByCustomerId(customerId, pageable);
        }

        return new PageResponse<>(result.getContent().stream().map(this::toResponse).toList(), result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public TicketResponse findById(Long customerId, Long ticketId) {
        SecurityUtils.requireCustomerAccess(customerId);
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket " + ticketId + " was not found for customer " + customerId));
        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse create(Long customerId, TicketRequest request) {
        if (SecurityUtils.isAdmin()) {
            Customer customer = getCustomer(customerId);
            return saveNewTicket(customer, request);
        }
        SecurityUtils.requireCustomerAccess(customerId);
        Customer customer = getCustomer(customerId);
        return saveNewTicket(customer, request);
     
    }

    @Transactional
    public TicketResponse update(Long customerId, Long ticketId, TicketRequest request) {
        SecurityUtils.requireCustomerAccess(customerId);
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket " + ticketId + " was not found for customer " + customerId));
        boolean statusChanged = ticket.getStatus() != request.status();
        ticket.update(request.subject().trim(), request.status(), request.priority());
        Ticket saved = ticketRepository.save(ticket);
 
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long customerId, Long ticketId) {
        if (!SecurityUtils.isAdmin()) {
            throw new ForbiddenException("Only ADMIN users can delete tickets");
        }
        Ticket ticket = ticketRepository.findByIdAndCustomerId(ticketId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket " + ticketId + " was not found for customer " + customerId));
        ticketRepository.delete(ticket);
    }

    private TicketResponse saveNewTicket(Customer customer, TicketRequest request) {
        Ticket saved = ticketRepository.save(new Ticket(customer, request.subject().trim(), request.status(), request.priority()));
  
        return toResponse(saved);
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " was not found"));
    }



    private void validatePage(int page, int size) {
        if (page < 0) throw new BadRequestException("page must be zero or greater");
        if (size < 1 || size > 100) throw new BadRequestException("size must be between 1 and 100");
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType, String parameterName) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Unsupported " + parameterName + " value: " + value);
        }
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.getId(), ticket.getCustomer().getId(), ticket.getSubject(), ticket.getStatus(), ticket.getPriority(), ticket.getCreatedAt(), ticket.getUpdatedAt());
    }
}
