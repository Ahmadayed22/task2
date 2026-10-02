package com.task2.portal.security;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.task2.portal.entity.User;
import com.task2.portal.repo.UserRepo;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepo repository;

    public AppUserDetailsService(UserRepo repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        return new AuthenticatedUser(user.getUsername(), user.getPassword(), user.getRole(),
                user.getCustomerId());
    }
}
