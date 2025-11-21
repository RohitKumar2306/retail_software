package com.ecom.retailsoftware.service.impl;

import com.ecom.retailsoftware.entity.UserEntity;
import com.ecom.retailsoftware.repository.UserRepository;
import com.ecom.retailsoftware.security.AppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found for the email: " + email));

        String roleName = existingUser.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));

        try {
            AppRole role = AppRole.valueOf(roleName);
            role.getAuthorities().forEach(authority ->
                    authorities.add(new SimpleGrantedAuthority(authority.toString()))
            );
        }  catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        return new User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                true, true, true, true,
                authorities
        );
    }
}
