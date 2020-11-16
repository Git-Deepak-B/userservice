package com.test.userservice.service;

import com.test.userservice.dto.RegisterUserRequestDTO;
import com.test.userservice.model.Role;
import com.test.userservice.model.User;
import com.test.userservice.repository.RoleRepository;
import com.test.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public AdminServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<?> registerUser(RegisterUserRequestDTO registerUserRequestDTO) {
        try {
            User user = User.builder()
                    .username(registerUserRequestDTO.getUsername())
                    .email(registerUserRequestDTO.getEmail())
                    .firstName(registerUserRequestDTO.getFirstName())
                    .lastName(registerUserRequestDTO.getLastName())
                    .enabled(registerUserRequestDTO.isEnabled())
                    .password(encoder.encode(registerUserRequestDTO.getPassword()))
                    .build();
            List<Role> roleList = new ArrayList<>();

            for (String roleName: registerUserRequestDTO.getRoleList()) {
                Optional<Role> role = roleRepository.findByName(roleName);
                if (role.isPresent()) {
                    roleList.add(role.get());
                } else {
                    return new ResponseEntity<>("Role Not Found :: " + roleName, HttpStatus.BAD_REQUEST);
                }
            }

            if (!roleList.isEmpty()) {
                user.setRoles(new HashSet<Role>(roleList));
            }

            return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);

        } catch (Exception ex) {
            logger.error("error while creating user :: " + ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getUserList() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found for given username " + username, HttpStatus.NOT_FOUND);
        }
    }
}
