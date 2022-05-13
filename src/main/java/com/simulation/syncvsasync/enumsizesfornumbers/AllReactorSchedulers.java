package com.simulation.syncvsasync.enumsizesfornumbers;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * The class All reactor Schedulers
 * @author rubn
 */
public enum AllReactorSchedulers {

    BOUNDED_ELASTIC(Schedulers.boundedElastic()),
    SINGLE(Schedulers.single()),
    IMMEDIATE(Schedulers.immediate()),
    PARALLEL(Schedulers.parallel());

    private Scheduler schedulers;

    AllReactorSchedulers(final Scheduler schedulers) {
        this.schedulers = schedulers;
    }

    public Scheduler getName() {
        return schedulers;
    }

    /**
     * Render icons in radio buttons
     *
     * @return ComponentRenderer<Div,AllReactorSchedulers> with render icon
     */
    public static ComponentRenderer<Div,AllReactorSchedulers> getIconRenderer() {
        final SerializableBiConsumer<Div,AllReactorSchedulers> sbc = (div, scheduler) -> {
            if(scheduler.getName() == BOUNDED_ELASTIC.getName()) {
                final Span span = new Span();
                span.setText(BOUNDED_ELASTIC.getName()+" 😁");
                div.add(span);
            } else if(scheduler.getName() == SINGLE.getName()){
                final Span span = new Span();
                span.setText(SINGLE.getName()+" 😭");
                div.add(span);
            } else if(scheduler.getName() == IMMEDIATE.getName()){
                final Span span = new Span();
                span.setText("Schedulers.immediate()".concat(" 😁"));
                div.add(span);
            } else {
                final Span span = new Span();
                span.setText(PARALLEL.getName()+" 😭");
                div.add(span);
            }
        };
        return new ComponentRenderer<>(Div::new, sbc);
    }


}
