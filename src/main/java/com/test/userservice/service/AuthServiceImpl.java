package com.test.userservice.service;

import com.test.userservice.config.Constants;
import com.test.userservice.config.JwtUtils;
import com.test.userservice.config.UserDetailsImpl;
import com.test.userservice.dto.JwtResponseDto;
import com.test.userservice.dto.LoginRequestDTO;
import com.test.userservice.model.BlacklistToken;
import com.test.userservice.repository.BlacklistTokenRepository;
import com.test.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            BlacklistTokenRepository blacklistTokenRepository,
            JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.blacklistTokenRepository = blacklistTokenRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ResponseEntity<?> login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        JwtResponseDto jwtResponse = new JwtResponseDto();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        jwtResponse.setToken(jwt);
        jwtResponse.setId(userDetails.getId());
        jwtResponse.setUsername(userDetails.getUsername());
        jwtResponse.setEmail(userDetails.getEmail());
        jwtResponse.setRoles(roles);
        jwtResponse.setType(Constants.TOKEN_TYPE);

        return ResponseEntity.ok(jwtResponse);
    }

    @Override
    public ResponseEntity<?> logout() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                            .getRequest();
            String headerAuth = request.getHeader("Authorization");
            if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                String token = headerAuth.substring(7, headerAuth.length());
                if (blacklistTokenRepository.existsByToken(token)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                blacklistTokenRepository.save(BlacklistToken.builder().token(token).build());
                return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
            } else {
                logger.info("unable to get Authorization header");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            logger.info("Exception while logging out ", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> fetchMe() {
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetailsImpl) {
            username = ((UserDetailsImpl)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return new ResponseEntity<>(userRepository.findByUsername(username), HttpStatus.OK);
    }
}
