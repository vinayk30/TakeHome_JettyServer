package com.org.property.data.server.web;

import org.jvnet.hk2.annotations.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class TaskExecutor implements Executor {

    private final Executor delegate = Executors.newFixedThreadPool(4);

    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }
}