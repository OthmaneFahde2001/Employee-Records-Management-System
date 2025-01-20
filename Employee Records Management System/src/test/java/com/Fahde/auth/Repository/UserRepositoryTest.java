package com.Fahde.auth.Repository;


import com.Fahde.auth.Entity.Role;
import com.Fahde.auth.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1)
                .username("testuser")
                .password("password")
                .role(Role.ADMINISTRATOR)
                .build();
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("password", foundUser.get().getPassword());
        assertEquals("IT", foundUser.get().getDepartment());
        assertEquals(Role.ADMINISTRATOR, foundUser.get().getRole());
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        assertTrue(foundUser.isEmpty());
    }
}
