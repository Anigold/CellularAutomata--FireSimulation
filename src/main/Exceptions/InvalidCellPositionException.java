package Exceptions;

public class InvalidCellPositionException extends Exception {
    public InvalidCellPositionException(String errorMessage) {
        super(errorMessage);
    }
}