package org.retal.plugin.test.exception;

public class FieldNotDeclaredException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE_FIELD =
            "Mojo %s has no field with name '%s'";

    public FieldNotDeclaredException(Class<?> clazz, String fieldName) {
        super(String.format(EXCEPTION_MESSAGE_FIELD, clazz.getName(), fieldName));
    }
}
