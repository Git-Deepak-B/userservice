package com.test.userservice.dto;

import com.test.userservice.validation.UniqueEmail;
import com.test.userservice.validation.UniqueUsername;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequestDTO {
    @NotNull
    @UniqueUsername
    private String username;
    private String firstName;
    private String lastName;
    @Email
    @UniqueEmail
    private String email;
    @NotNull
    @NotBlank
    private String password;
    @NotEmpty
    private List<String> roleList;
    private boolean enabled = true;
}
