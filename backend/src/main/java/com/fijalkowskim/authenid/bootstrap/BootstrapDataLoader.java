package com.fijalkowskim.authenid.bootstrap;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapDataLoader implements ApplicationRunner {

    private final List<BootstrapTask> tasks;

    @Override
    public void run(ApplicationArguments args) {
        tasks.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .forEach(BootstrapTask::run);
    }
}
