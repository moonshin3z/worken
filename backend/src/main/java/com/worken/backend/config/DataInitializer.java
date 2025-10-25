package com.worken.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.worken.backend.job.Job;
import com.worken.backend.job.JobRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedJobs(JobRepository jobRepository) {
        return args -> {
            if (jobRepository.count() > 0) {
                return;
            }

            jobRepository.save(new Job(
                    "Niñera por horas",
                    "Familia en barrio Palermo busca niñera responsable para cuidar a dos niños de 4 y 6 años durante las tardes.",
                    "Palermo, Buenos Aires",
                    2500,
                    "Cuidado"
            ));

            jobRepository.save(new Job(
                    "Ayudante de cocina",
                    "Restaurante local necesita apoyo en la preparación de platos y limpieza del área de trabajo.",
                    "Centro, Córdoba",
                    2200,
                    "Gastronomía"
            ));

            jobRepository.save(new Job(
                    "Pintor de interiores",
                    "Vecino necesita pintar departamento de dos ambientes. Se proveen materiales.",
                    "Rosario, Santa Fe",
                    3000,
                    "Oficios"
            ));

            jobRepository.save(new Job(
                    "Paseador de perros",
                    "Se buscan paseadores para turnos matutinos de lunes a viernes.",
                    "Belgrano, Buenos Aires",
                    1800,
                    "Mascotas"
            ));
        };
    }
}
