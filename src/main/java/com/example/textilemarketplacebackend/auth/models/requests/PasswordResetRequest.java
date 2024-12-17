package com.example.textilemarketplacebackend.auth.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    private String new_password;
}
