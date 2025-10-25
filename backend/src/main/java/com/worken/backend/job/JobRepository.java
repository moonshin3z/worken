package com.worken.backend.job;

import java.util.List;
import java.util.Optional;

public interface JobRepository {
    List<Job> findAll();

    Optional<Job> findById(long id);

    Job save(Job job);

    void delete(long id);
}
