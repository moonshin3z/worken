package com.worken.backend.job;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(Long id) {
        super("No se encontró el trabajo con id " + id);
    }
}
