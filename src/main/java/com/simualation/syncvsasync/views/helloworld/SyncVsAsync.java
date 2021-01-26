package com.simualation.syncvsasync.views.helloworld;

import com.simualation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simualation.syncvsasync.service.MemoryConsumption;
import com.simualation.syncvsasync.service.SyncRandomNumbers;
import com.simualation.syncvsasync.service.AsyncRandomNumbersWithFlux;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.simualation.syncvsasync.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import java.util.stream.Stream;

/**
 *
 */
@Log4j2
@Route(value = "Sync-vs-Async", layout = MainView.class)
@PageTitle("Execute frecuency sync vs async")
//@CssImport("./styles/views/helloworld/hello-world-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class SyncVsAsync extends VerticalLayout {

    private final ComboBox<EnumSizeForRandomNumbers> syncComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> reactiveComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> asyncComboBoxWithCompletableFuture = new ComboBox<>();

    /**
     * Sync version
     */
    private SyncRandomNumbers syncRandomNumbers;

    /**
     * Async version
     */
    private AsyncRandomNumbersWithFlux asyncRandomNumbersWithFlux;

    private MemoryConsumption memoryConsumption;

    @Autowired
    public SyncVsAsync(SyncRandomNumbers syncRandomNumbers,
                       AsyncRandomNumbersWithFlux asyncRandomNumbersWithFlux,
                       final MemoryConsumption memoryConsumption) {

        this.syncRandomNumbers = syncRandomNumbers;
        this.asyncRandomNumbersWithFlux = asyncRandomNumbersWithFlux;
        this.memoryConsumption = memoryConsumption;

        this.initLayout();
    }

    private void initLayout() {
        syncComboBox.focus();

        syncComboBox.setLabel("Sync frecuency");
        asyncComboBoxWithCompletableFuture.setLabel("Async frecuency with CompletableFuture");
        reactiveComboBox.setLabel("Async frecuency with Project reactor");
        asyncComboBoxWithCompletableFuture.setWidthFull();
        syncComboBox.setItems(EnumSizeForRandomNumbers.values());
        reactiveComboBox.setItems(EnumSizeForRandomNumbers.values());
        asyncComboBoxWithCompletableFuture.setItems(EnumSizeForRandomNumbers.values());

        Stream.of(syncComboBox, reactiveComboBox, asyncComboBoxWithCompletableFuture)
                .forEach(e -> {
                    e.setPlaceholder("Choose stream size");
                    e.setWidth("50%");
                });
        syncComboBox.setItemLabelGenerator(EnumSizeForRandomNumbers::getItemLabel);
        reactiveComboBox.setItemLabelGenerator(EnumSizeForRandomNumbers::getItemLabel);
        asyncComboBoxWithCompletableFuture.setItemLabelGenerator(EnumSizeForRandomNumbers::getItemLabel);

        this.initSyncFrecuency();

        this.add(syncComboBox, asyncComboBoxWithCompletableFuture, reactiveComboBox);
    }

    private void initSyncFrecuency() {
        syncComboBox.addValueChangeListener(event -> {
            this.execute(event.getValue().getSize(),
                    e -> syncRandomNumbers.syncFrencuency(event.getValue().getSize()));

        });

    }

    private void initWithCompletableFuture(final Optional<UI> optionalUI) {
        asyncComboBoxWithCompletableFuture.addValueChangeListener(event -> {
            CompletableFuture.supplyAsync(() -> this.syncRandomNumbers.syncFrencuency(event.getValue().getSize()))
                    .whenCompleteAsync((map, error) -> {
                        optionalUI.ifPresent(ui -> {
                            ui.access(() -> {
                                this.execute(event.getValue().getSize(),
                                        e -> map);
                            });
                        });
                    });
        });
    }

    private void initAsyncFrecuencyWithFlux(final Optional<UI> optionalUI) {
        reactiveComboBox.addValueChangeListener(event -> {
            Flux.defer(() -> this.asyncRandomNumbersWithFlux.fluxFrecuency(event.getValue().getSize()))
                    .delayElements(Duration.ofMillis(10))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(subscripbeMap -> {
                        optionalUI.ifPresent(ui -> {
                            ui.access(() -> {
                                this.execute(event.getValue().getSize(),
                                        e -> subscripbeMap);
                            });
                        });
                    });
        });
    }

    private void execute(final Long size, final LongFunction<Map<Integer, Long>> funciontFrecuency) {
        if (size.equals(EnumSizeForRandomNumbers.FIVE_MILLION.getSize())) {
            final Paragraph p1 = new Paragraph("Generated Five millions iterations");
            final Paragraph p2 = new Paragraph(funciontFrecuency.apply(size).toString());
            final Paragraph p3 = new Paragraph(this.memoryConsumption.getTotalMemory());
            final Notification n = new Notification(p1,p2,p3) ;
            n.setPosition(Position.MIDDLE);
            n.setDuration(2500);
            n.open();
            System.gc();
        } else {
            final Paragraph p1 = new Paragraph("Generated Ten millions iterations");
            final Paragraph p2 = new Paragraph(funciontFrecuency.apply(size).toString());
            final Paragraph p3 = new Paragraph(this.memoryConsumption.getTotalMemory());
            final Notification n = new Notification(p1,p2,p3) ;
            n.setPosition(Position.MIDDLE);
            n.setDuration(2500);
            n.open();
            System.gc();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.initAsyncFrecuencyWithFlux(this.getUI());
        this.initWithCompletableFuture(this.getUI());
    }
}
