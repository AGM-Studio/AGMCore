package me.ashenguard.exceptions;

public class IllegalFormatException extends IllegalArgumentException {
    public IllegalFormatException() {
        super("Version format is invalid.");
    }
}
