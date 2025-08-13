package com.example.taskmanager.service;

public class DomainObjectValidationException extends Exception {
    public DomainObjectValidationException(String message) {
        super(message);
    }
}
