package com.worken.backend.job;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class JobService {

    private final JobRepository repository;

    public JobService(JobRepository repository) {
        this.repository = repository;
    }

    public List<Job> findAll() {
        return repository.findAll();
    }

    public Optional<Job> findById(long id) {
        return repository.findById(id);
    }

    public Job create(JobInput input) {
        Job job = new Job(0,
                input.title(),
                input.description(),
                input.category(),
                input.city(),
                input.payment(),
                input.contactPhone(),
                input.contactEmail(),
                ensureDate(input.publishedAt()));
        return repository.save(job);
    }

    public Optional<Job> update(long id, JobInput input) {
        return repository.findById(id).map(existing -> {
            Job updated = existing.withUpdatedFields(new JobInput(
                    input.title(),
                    input.description(),
                    input.category(),
                    input.city(),
                    input.payment(),
                    input.contactPhone(),
                    input.contactEmail(),
                    ensureDate(input.publishedAt())
            ));
            return repository.save(updated);
        });
    }

    public boolean delete(long id) {
        boolean present = repository.findById(id).isPresent();
        if (present) {
            repository.delete(id);
        }
        return present;
    }

    private LocalDate ensureDate(LocalDate publishedAt) {
        return publishedAt != null ? publishedAt : LocalDate.now();
    }
}
