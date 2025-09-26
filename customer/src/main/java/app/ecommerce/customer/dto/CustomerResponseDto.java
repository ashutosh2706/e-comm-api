package app.ecommerce.customer.dto;

import java.util.List;

public record CustomerResponseDto<T>(
        int page,
        int pageSize,
        long totalRecords,
        List<T> data
) {}
