package com.task2.portal.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.task2.portal.entity.Customer;
import com.task2.portal.entity.OrderEntity;
import com.task2.portal.entity.Ticket;
import com.task2.portal.entity.User;
import com.task2.portal.enums.OrderStatus;
import com.task2.portal.enums.Priority;
import com.task2.portal.enums.Role;
import com.task2.portal.enums.TicketStatus;
import com.task2.portal.repo.CustomerRepo;
import com.task2.portal.repo.OrderRepo;
import com.task2.portal.repo.TicketRepo;
import com.task2.portal.repo.UserRepo;

@Configuration 
public class DataInitializer {

    @Bean 
    CommandLineRunner seedData(
            CustomerRepo customerRepository,
            OrderRepo orderRepository,
            TicketRepo ticketRepository,
            UserRepo UserRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (customerRepository.count() > 0) return;

            Customer acme = customerRepository.save(new Customer("Acme Corporation", "acme@example.com", "+962790000001"));
            Customer globex = customerRepository.save(new Customer("Globex LLC", "globex@example.com", "+962790000002"));
            Customer initech = customerRepository.save(new Customer("Initech", "initech@example.com", "+962790000003"));

            orderRepository.saveAll(List.of(
                    new OrderEntity(acme, List.of("Laptop", "Docking Station"), OrderStatus.PENDING, Priority.HIGH, new BigDecimal("1250.00")),
                    new OrderEntity(acme, List.of("Keyboard", "Mouse"), OrderStatus.SHIPPED, Priority.MEDIUM, new BigDecimal("145.00")),
                    new OrderEntity(globex, List.of("Monitor"), OrderStatus.CONFIRMED, Priority.MEDIUM, new BigDecimal("320.00")),
                    new OrderEntity(initech, List.of("Headset"), OrderStatus.DELIVERED, Priority.LOW, new BigDecimal("90.00"))
            ));

            ticketRepository.saveAll(List.of(
                    new Ticket(acme, "Cannot connect to VPN", TicketStatus.OPEN, Priority.HIGH),
                    new Ticket(acme, "Need invoice copy", TicketStatus.IN_PROGRESS, Priority.MEDIUM),
                    new Ticket(globex, "Change delivery address", TicketStatus.OPEN, Priority.LOW),
                    new Ticket(initech, "Account access issue", TicketStatus.RESOLVED, Priority.HIGH)
            ));

            UserRepository.save(new User("agent01", passwordEncoder.encode("Admin123!"), Role.ADMIN, null));
            UserRepository.save(new User("customer01", passwordEncoder.encode("Customer123!"), Role.CUSTOMER, acme.getId()));
            UserRepository.save(new User("customer02", passwordEncoder.encode("Customer123!"), Role.CUSTOMER, globex.getId()));
            UserRepository.save(new User("customer03", passwordEncoder.encode("Customer123!"), Role.CUSTOMER, initech.getId()));
        };
    }
}
