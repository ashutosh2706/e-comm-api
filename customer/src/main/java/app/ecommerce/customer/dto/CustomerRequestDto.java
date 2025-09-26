package app.ecommerce.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDto (
        @NotNull(message = "Customer firstname is required")
        @NotBlank(message = "Customer firstname is required")
        String firstName,
        @NotNull(message = "Customer lastname is required")
        @NotBlank(message = "Customer lastname is required")
        String lastName,
        @Email(message = "email should be valid")
        @NotNull(message = "Customer email can't be blank")
        String email
) {}
