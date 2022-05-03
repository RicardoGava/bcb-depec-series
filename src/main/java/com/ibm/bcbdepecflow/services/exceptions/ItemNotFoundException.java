package com.ibm.bcbdepecflow.services.exceptions;

public class ItemNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(Object id) {
        super("Item not found. Id: " + id);
    }
}