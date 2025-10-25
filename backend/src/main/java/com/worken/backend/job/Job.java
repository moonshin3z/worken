package com.worken.backend.job;

import java.time.LocalDate;
import java.util.Objects;

public class Job {
    private final long id;
    private final String title;
    private final String description;
    private final String category;
    private final String city;
    private final double payment;
    private final String contactPhone;
    private final String contactEmail;
    private final LocalDate publishedAt;

    public Job(long id, String title, String description, String category, String city, double payment,
               String contactPhone, String contactEmail, LocalDate publishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.city = city;
        this.payment = payment;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.publishedAt = publishedAt;
    }

    public long getId() {
        return id;
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

    public Job withUpdatedFields(JobInput input) {
        return new Job(
                this.id,
                input.getTitle(),
                input.getDescription(),
                input.getCategory(),
                input.getCity(),
                input.getPayment(),
                input.getContactPhone(),
                input.getContactEmail(),
                input.getPublishedAt()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return id == job.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
