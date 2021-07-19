package com.simulation.syncvsasync.views.helloworld;

import com.simulation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simulation.syncvsasync.service.MemoryConsumption;
import com.simulation.syncvsasync.service.SyncRandomNumbers;
import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import java.util.stream.Stream;

/**
 * @author rubn
 */
@Log4j2
@Route(value = "Sync-vs-Async", layout = MainView.class)
@PageTitle("Execute frecuency sync vs async")
@RouteAlias(value = "", layout = MainView.class)
@RequiredArgsConstructor
public class SyncVsAsync extends VerticalLayout {

    private final ComboBox<EnumSizeForRandomNumbers> syncComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> reactiveComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> asyncComboBoxWithCompletableFuture = new ComboBox<>();

    /**
     * Sync version
     */
    private final SyncRandomNumbers syncRandomNumbers;
    /**
     * Reactive version
     */
    private final ReactiveRandomNumbers reactiveRandomNumbers;
    private final MemoryConsumption memoryConsumption;

    @PostConstruct
    private void initLayout() {
        syncComboBox.focus();

        syncComboBox.setLabel("Sync frecuency");
        asyncComboBoxWithCompletableFuture.setLabel("Async frecuency with CompletableFuture");
        reactiveComboBox.setLabel("Reactive frecuency with Project reactor");
        asyncComboBoxWithCompletableFuture.setWidthFull();
        syncComboBox.setItems(EnumSizeForRandomNumbers.values());
        reactiveComboBox.setItems(EnumSizeForRandomNumbers.values());
        asyncComboBoxWithCompletableFuture.setItems(EnumSizeForRandomNumbers.values());

        Stream.of(syncComboBox, reactiveComboBox, asyncComboBoxWithCompletableFuture)
                .forEach(e -> {
                    e.setPlaceholder("Choose stream size");
                    e.setWidth("50%");
                    e.setItemLabelGenerator(EnumSizeForRandomNumbers::getItemLabel);
                });

        this.initSyncFrecuency();
        this.add(syncComboBox, asyncComboBoxWithCompletableFuture, reactiveComboBox);
    }

    private void initSyncFrecuency() {
        syncComboBox.addValueChangeListener(event -> {
            this.execute(event.getValue().getSize(),
                    e -> syncRandomNumbers.syncFrencuency(event.getValue().getSize()));

        });
    }

    private void initWithCompletableFuture(final UI ui) {
        asyncComboBoxWithCompletableFuture.addValueChangeListener(event -> {
            CompletableFuture.supplyAsync(() -> this.syncRandomNumbers.syncFrencuency(event.getValue().getSize()))
                    .whenCompleteAsync((map, error) -> {
                        ui.access(() -> {
                            this.execute(event.getValue().getSize(), e -> map);
                        });
                    });
        });
    }

    private void initReactiveFrecuency(final UI ui) {
        reactiveComboBox.addValueChangeListener(event -> {
            Flux.defer(() -> this.reactiveRandomNumbers.fluxFrecuency(event.getValue().getSize()))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(subscripbeMap -> {
                        ui.access(() -> {
                            this.execute(event.getValue().getSize(), e -> subscripbeMap);
                        });
                    });
        });
    }

    private void execute(final Long size, final LongFunction<Map<Integer, Long>> funciontFrecuency) {
        if (size.equals(EnumSizeForRandomNumbers.FIVE_MILLION.getSize())) {
            notification(size, funciontFrecuency, "Generated Five millions iterations");
        } else {
            notification(size, funciontFrecuency, "Generated Ten millions iterations");
        }
    }

    private void notification(final Long size, LongFunction<Map<Integer, Long>> funciontFrecuency, String text) {
        final Paragraph p1 = new Paragraph(text);
        final Paragraph p2 = new Paragraph(funciontFrecuency.apply(size).toString());
        final Paragraph p3 = new Paragraph(this.memoryConsumption.getTotalMemory());
        final Notification n = new Notification(p1, p2, p3);
        n.setPosition(Position.MIDDLE);
        n.setDuration(2500);
        n.open();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.getUI().ifPresent(ui -> {
            this.initReactiveFrecuency(ui);
            this.initWithCompletableFuture(ui);
        });

    }
}
