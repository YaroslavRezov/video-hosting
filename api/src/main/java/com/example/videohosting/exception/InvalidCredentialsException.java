package com.example.videohosting.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("Invalid username or password"); }
}
