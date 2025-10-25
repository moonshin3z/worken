package com.worken.backend.job;

import java.time.LocalDate;

/**
 * Representa los datos enviados por el cliente al crear o actualizar un trabajo.
 * Mantiene compatibilidad con versiones antiguas de Java evitando el uso de records.
 */
public class JobRequest {

    private final String title;
    private final String description;
    private final String category;
    private final String city;
    private final double payment;
    private final String contactPhone;
    private final String contactEmail;
    private final LocalDate publishedAt;

    public JobRequest(String title,
                      String description,
                      String category,
                      String city,
                      double payment,
                      String contactPhone,
                      String contactEmail,
                      LocalDate publishedAt) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.city = city;
        this.payment = payment;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getCity() {
        return city;
    }

    public double getPayment() {
        return payment;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }

    public JobInput toInput() {
        return new JobInput(title, description, category, city, payment, contactPhone, contactEmail, publishedAt);
    }
}
