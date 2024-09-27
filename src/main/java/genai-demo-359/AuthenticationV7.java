 ```java
package com.mycompany.api.controller;

import com.mycompany.api.model.User;
import com.mycompany.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint for user login.
     * 
     * @param username Username to login.
     * @param password Password for the user.
     * @return ResponseEntity with user details if login is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody User user) {
        // Validate user input to prevent injection attacks
        if (username == null || password == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User authenticatedUser = userService.authenticateUser(username, password);
            return ResponseEntity.ok(authenticatedUser);
        } catch (UserNotFoundException e) {
            // Log error for debugging purposes
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            // Log error for debugging purposes
            return ResponseEntity.status(500).body(null);
        }
    }
}
```

```java
package com.mycompany.api.service;
import com.mycompany.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class UserService {

    /**
     * Authenticates the user with the given username and password.
     * 
     * @param username Username to authenticate.
     * @param password Password to authenticate.
     * @return User if authentication is successful, null otherwise.
     */
    public User authenticateUser(String username, String password) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found with username: " + username);
        }

        // Use prepared statement to prevent SQL injection
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return user;
                }
            }
        } catch (SQLException e) {
            // Log error for debugging purposes
            throw new RuntimeException("Error while authenticating user", e);
        }

        return null;
    }
}
```

```java
package com.mycompany.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password; // Avoid hardcoding sensitive information

    // Getters and setters
}
```

```java
package com.mycompany.api.repository;
import com.mycompany.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
```

```java
package com.mycompany.api.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

```java
package com.mycompany.api.exception;

public class InvalidUserCredentialsException extends RuntimeException {
    public InvalidUserCredentialsException(String message) {
        super(message);
    }
}
```