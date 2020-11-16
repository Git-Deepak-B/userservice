package com.test.userservice.runner;

import com.test.userservice.config.Constants;
import com.test.userservice.model.Role;
import com.test.userservice.model.User;
import com.test.userservice.repository.RoleRepository;
import com.test.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder encoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.findByName(Constants.ROLE_ADMIN).isPresent()) {
            roleRepository.save(Role.builder().name(Constants.ROLE_ADMIN).build());
            logger.info("created ROLE_ADMIN");
        }

        if (!roleRepository.findByName(Constants.ROLE_USER).isPresent()) {
            roleRepository.save(Role.builder().name(Constants.ROLE_USER).build());
            logger.info("created ROLE_USER");
        }

        if (!userRepository.findByUsername("admin").isPresent()) {
            Optional<Role> role = roleRepository.findByName(Constants.ROLE_ADMIN);
            User user = new User();
            user.setFirstName("admin");
            user.setLastName("admin");
            user.setEmail("admin@userservice.com");
            user.setUsername(Constants.ADMIN_USERNAME);
            user.setPassword(encoder.encode(Constants.ADMIN_PASSWORD));
            user.setEnabled(true);
            role.ifPresent(value -> user.setRoles(new HashSet<Role>(Collections.singletonList(value))));
            userRepository.save(user);
            logger.info("created admin user");
        }
    }
}
