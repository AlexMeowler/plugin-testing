package org.retal.plugin.test.logging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.retal.plugin.test.exception.ExceptionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncOutputRedirector extends Thread {

    private final Log log;

    private final InputStream inputStream;

    private final InputStream errorStream;

    private final ExceptionUtil exceptionUtil;

    public AsyncOutputRedirector(Log log, InputStream inputStream, InputStream errorStream, ExceptionUtil exceptionUtil) {
        super();
        this.log = log;
        this.inputStream = inputStream;
        this.errorStream = errorStream;
        this.exceptionUtil = exceptionUtil;
    }

    @Override
    public void run() {
        try(BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream))) {
            while (true) {
                if(inputReader.ready()) {
                    log.info(inputReader.readLine());

                }
                if(errorReader.ready()) {
                    exceptionUtil.failOrLog(errorReader.readLine());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MojoExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
