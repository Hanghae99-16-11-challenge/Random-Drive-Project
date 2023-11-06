//package com.example.randomdriveproject.user.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Optional;
//
//import com.example.randomdriveproject.user.entity.User;
//import com.example.randomdriveproject.user.entity.UserRoleEnum;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void findByUsername() {
//        // Given
//        User user = new User("testUser", "password123", "test@example.com", UserRoleEnum.USER);
//        userRepository.save(user);
//
//        // When
//        Optional<User> foundUser = userRepository.findByUsername("testUser");
//
//        // Then
//        assertTrue(foundUser.isPresent());
//        assertEquals("test@example.com", foundUser.get().getEmail());
//        assertEquals(UserRoleEnum.USER, foundUser.get().getUserRole());
//    }
//
//    @Test
//    void findByEmail() {
//        // Given
//        User user = new User("testUser", "password123", "test@example.com", UserRoleEnum.USER);
//        userRepository.save(user);
//
//        // When
//        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
//
//        // Then
//        assertTrue(foundUser.isPresent());
//        assertEquals("testUser", foundUser.get().getUsername());
//        assertEquals(UserRoleEnum.USER, foundUser.get().getUserRole());
//    }
//
//    @Test
//    void findByKakaoId() {
//        // Given
//        User user = new User("testUser", "password123", "test@example.com", UserRoleEnum.USER);
//        user.setKakaoId(12345L);
//        userRepository.save(user);
//
//        // When
//        Optional<User> foundUser = userRepository.findByKakaoId(12345L);
//
//        // Then
//        assertTrue(foundUser.isPresent());
//        assertEquals("testUser", foundUser.get().getUsername());
//        assertEquals(12345L, foundUser.get().getKakaoId());
//        assertEquals(UserRoleEnum.USER, foundUser.get().getUserRole());
//    }
//
//    @Test
//    void findByUsername_WhenUserNotFound() {
//        // Given
//        String nonExistentUsername = "nonexistentUser";
//
//        // When
//        Optional<User> foundUser = userRepository.findByUsername(nonExistentUsername);
//
//        // Then
//        assertFalse(foundUser.isPresent());
//    }
//
//    @Test
//    void findByEmail_WhenUserNotFound() {
//        // Given
//        String nonExistentEmail = "nonexistent@example.com";
//
//        // When
//        Optional<User> foundUser = userRepository.findByEmail(nonExistentEmail);
//
//        // Then
//        assertFalse(foundUser.isPresent());
//    }
//
//    @Test
//    void findByKakaoId_WhenUserNotFound() {
//        // Given
//        Long nonExistentKakaoId = 9999L;
//
//        // When
//        Optional<User> foundUser = userRepository.findByKakaoId(nonExistentKakaoId);
//
//        // Then
//        assertFalse(foundUser.isPresent());
//    }
//}