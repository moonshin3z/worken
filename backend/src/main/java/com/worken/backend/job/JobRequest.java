package com.worken.backend.job;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 1000) String description,
        @NotBlank @Size(max = 255) String location,
        @NotNull @Min(0) Integer hourlyRate,
        @NotBlank @Size(max = 255) String category
) {}
