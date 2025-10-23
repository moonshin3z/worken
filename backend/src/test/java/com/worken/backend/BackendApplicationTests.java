package com.worken.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.worken.backend.job.Job;
import com.worken.backend.job.JobService;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private JobService jobService;

    @Test
    void contextLoadsAndSeedsData() {
        List<Job> jobs = jobService.findAll();
        assertThat(jobs)
                .isNotEmpty()
                .allSatisfy(job -> assertThat(job.getId()).isNotNull());
    }
}
