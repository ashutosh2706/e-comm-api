package app.ecommerce.order.dto.customer;

public record CustomerResponseDTO(
        long id,
        String firstName,
        String lastName,
        String email
) {
}
