package com.edu.imageconversion.services;

public class ImageConversionRuntimeException
        extends RuntimeException {

    private final int index;

    public ImageConversionRuntimeException(
            int index,
            Throwable cause
    ) {
        super(cause);
        this.index = index;
    }

    public int index() {
        return index;
    }
}