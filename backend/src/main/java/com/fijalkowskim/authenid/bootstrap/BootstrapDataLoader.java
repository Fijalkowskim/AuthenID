package com.fijalkowskim.authenid.bootstrap;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapDataLoader implements ApplicationRunner {

    private final List<BootstrapTask> tasks;

    /**
     * Runs all bootstrap tasks after the application context is initialized.
     */
    @Override
    public void run(ApplicationArguments args) {
        for (BootstrapTask task : tasks) {
            task.run();
        }
    }
}
