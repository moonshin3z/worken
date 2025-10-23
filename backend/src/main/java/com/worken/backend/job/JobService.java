package com.worken.backend.job;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    public Job create(JobRequest request) {
        Job job = new Job(
                request.title(),
                request.description(),
                request.location(),
                request.hourlyRate(),
                request.category());
        return jobRepository.save(job);
    }

    public Job update(Long id, JobRequest request) {
        Job job = findById(id);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setLocation(request.location());
        job.setHourlyRate(request.hourlyRate());
        job.setCategory(request.category());
        return job;
    }

    public void delete(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new JobNotFoundException(id);
        }
        jobRepository.deleteById(id);
    }
}
