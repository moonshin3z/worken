package com.worken.backend.job;

import java.time.LocalDate;

public record JobInput(
        String title,
        String description,
        String category,
        String city,
        double payment,
        String contactPhone,
        String contactEmail,
        LocalDate publishedAt
) {
}
