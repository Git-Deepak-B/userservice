package com.test.userservice.repository;

import com.test.userservice.model.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, Long> {
    boolean existsByToken(String token);
}
