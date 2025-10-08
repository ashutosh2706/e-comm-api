package app.ecommerce.customer.dto;

import app.ecommerce.customer.entity.CustomerRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerRequestDto (
        @NotNull(message = "Customer firstname is required")
        @NotBlank(message = "Customer firstname is required")
        String firstName,
        @NotNull(message = "Customer lastname is required")
        @NotBlank(message = "Customer lastname is required")
        String lastName,
        @Email(message = "email should be valid")
        @NotNull(message = "Customer email can't be blank")
        String email,
        @NotNull(message = "Customer roleId is required")
        @Positive(message = "Customer roleId must be valid")
        Integer roleId
) {}
