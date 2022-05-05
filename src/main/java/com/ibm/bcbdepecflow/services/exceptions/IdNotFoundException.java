package com.ibm.bcbdepecflow.services.exceptions;

public class IdNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IdNotFoundException(Object id) {
        super("Item not found. Id: " + id);
    }
}