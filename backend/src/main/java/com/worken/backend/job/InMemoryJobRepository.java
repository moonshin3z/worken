package com.worken.backend.job;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryJobRepository implements JobRepository {

    private final Map<Long, Job> storage = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Job> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(storage.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Job> findById(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(storage.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Job save(Job job) {
        lock.writeLock().lock();
        try {
            Job toSave = job;
            if (job.getId() == 0) {
                long id = sequence.incrementAndGet();
                toSave = new Job(id, job.getTitle(), job.getDescription(), job.getCategory(), job.getCity(),
                        job.getPayment(), job.getContactPhone(), job.getContactEmail(), job.getPublishedAt());
            }
            storage.put(toSave.getId(), toSave);
            return toSave;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(long id) {
        lock.writeLock().lock();
        try {
            storage.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
