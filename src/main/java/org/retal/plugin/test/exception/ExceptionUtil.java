package org.retal.plugin.test.exception;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.util.Objects;

public class ExceptionUtil {

    private final boolean failOnError;

    private final Log log;

    public ExceptionUtil(boolean failOnError, Log log) {
        this.failOnError = failOnError;
        this.log = log;
    }

    public void failOrLog(Throwable throwable) throws MojoExecutionException {
        failOrLog(throwable, null);
    }

    public void failOrLog(String text) throws MojoExecutionException {
        failOrLog(null, text);
    }

    public void failOrLog(Throwable throwable, String text) throws MojoExecutionException {
        if(failOnError) {
            MojoExecutionException mojoExecutionException =
                    text == null ? new MojoExecutionException(throwable) :
                    new MojoExecutionException(text, throwable);
            throw mojoExecutionException;
        }
        if(Objects.nonNull(throwable) && Objects.nonNull(text)) {
            log.error(text, throwable);
        } else if (Objects.nonNull(throwable)) {
            log.error(throwable);
        } else if (Objects.nonNull(text)) {
            log.error(text);
        }
    }
}
