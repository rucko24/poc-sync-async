package com.simulation.syncvsasync.enumsizesfornumbers;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

/**
 * The class All reactor Schedulers
 *
 * @author rubn
 */
@Getter
@RequiredArgsConstructor
public enum AllReactorSchedulersAndVirtualThreads {

    BOUNDED_ELASTIC(Schedulers.boundedElastic()),
    SINGLE(Schedulers.single()),
    IMMEDIATE(Schedulers.immediate()),
    PARALLEL(Schedulers.parallel()),
    VIRTUAL_THREAD_PER_TASK_EXECUTOR(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor())),
    THREAD_PER_TASK_EXECUTOR(Schedulers.fromExecutor(Executors.newThreadPerTaskExecutor(Thread
            .ofVirtual()
            .name("threadPerTaskExecutor-")
            .factory())));

    final Scheduler schedulers;

    /**
     * Render icons in radio buttons
     *
     * @return ComponentRenderer<Div, AllReactorSchedulers> with render icon
     */
    public static ComponentRenderer<Div, AllReactorSchedulersAndVirtualThreads> getIconRenderer() {
        final SerializableBiConsumer<Div, AllReactorSchedulersAndVirtualThreads> sbc = (div, scheduler) -> {
            if (scheduler.getSchedulers() == BOUNDED_ELASTIC.getSchedulers()) {
                final Span span = new Span();
                span.setText(BOUNDED_ELASTIC.getSchedulers() + " 😁");
                div.add(span);
            } else if (scheduler.getSchedulers() == SINGLE.getSchedulers()) {
                final Span span = new Span();
                span.setText(SINGLE.getSchedulers() + " 😭");
                div.add(span);
            } else if (scheduler.getSchedulers() == IMMEDIATE.getSchedulers()) {
                final Span span = new Span();
                span.setText("Schedulers.immediate()".concat(" 😁"));
                div.add(span);
            } else if (scheduler.getSchedulers() == VIRTUAL_THREAD_PER_TASK_EXECUTOR.getSchedulers()) {
                final Span span = new Span();
                span.setText("Executors.newVirtualThreadPerTaskExecutor()" + " 🔥");
                div.add(span);
            } else if (scheduler.getSchedulers() == PARALLEL.schedulers) {
                final Span span = new Span();
                span.setText(PARALLEL.getSchedulers() + " 😭");
                div.add(span);
            } else {
                final Span span = new Span();
                span.setText("Executors.newThreadPerTaskExecutor() + factory" + " 🔥");
                div.add(span);
            }
        };
        return new ComponentRenderer<>(Div::new, sbc);
    }


}
