//package com.example.randomdriveproject.history.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.example.randomdriveproject.history.entity.Route;
//import com.example.randomdriveproject.user.entity.User;
//import com.example.randomdriveproject.user.entity.UserRoleEnum;
//import com.example.randomdriveproject.user.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.List;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class RouteRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RouteRepository routeRepository;
//
//    @Test
//    void findAllByUserId() {
//        // Given
//        User user = new User("testUser", "password123", "test@example.com", UserRoleEnum.USER);
//        userRepository.save(user);
//
//        Route route1 = new Route("Origin 1", "Destination 1", "MapType 1", 30, 50, user);
//        Route route2 = new Route("Origin 2", "Destination 2", "MapType 2", 40, 60, user);
//
//        routeRepository.save(route1);
//        routeRepository.save(route2);
//
//        // When
//        List<Route> routes = routeRepository.findAllByUserId(user.getId());
//
//        // Then
//        assertNotNull(routes);
//        assertEquals(2, routes.size());
//        assertEquals("Origin 1", routes.get(0).getOriginAddress());
//        assertEquals("Destination 2", routes.get(1).getDestinationAddress());
//    }
//
//    @Test
//    void findAllByUserId_NoRoutesFound() {
//        // Given
//        User user = new User("testUser", "password123", "test@example.com", UserRoleEnum.USER);
//        userRepository.save(user);
//
//        // When
//        List<Route> routes = routeRepository.findAllByUserId(user.getId());
//
//        // Then
//        assertNotNull(routes);
//        assertTrue(routes.isEmpty());
//    }
//}