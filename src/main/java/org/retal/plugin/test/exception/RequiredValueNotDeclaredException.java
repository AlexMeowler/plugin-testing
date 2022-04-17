package org.retal.plugin.test.exception;

public class RequiredValueNotDeclaredException extends RuntimeException {

    public RequiredValueNotDeclaredException() {
        super();
    }

    public RequiredValueNotDeclaredException(String message) {
        super(message);
    }
}
