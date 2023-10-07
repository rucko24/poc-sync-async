package com.simulation.syncvsasync.views.helloworld;

import com.simulation.syncvsasync.enumsizesfornumbers.AllReactorSchedulersAndVirtualThreads;
import com.simulation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simulation.syncvsasync.service.MemoryConsumption;
import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import com.simulation.syncvsasync.service.SyncRandomNumbers;
import com.simulation.syncvsasync.util.NotificationsUtils;
import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;


/**
 * @author rubn
 */
@Log4j2
@Route(value = "sync-async-reactive", layout = MainView.class)
@PageTitle("Execute frecuency sync - async - reactive")
@RouteAlias(value = "", layout = MainView.class)
@RequiredArgsConstructor
public class SyncAsyncReactiveView extends VerticalLayout implements NotificationsUtils {

    private final ComboBox<EnumSizeForRandomNumbers> syncComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> reactiveComboBox = new ComboBox<>();
    private final ComboBox<EnumSizeForRandomNumbers> asyncComboBoxWithCompletableFuture = new ComboBox<>();
    private final RadioButtonGroup<AllReactorSchedulersAndVirtualThreads> radioButtonGroup = new RadioButtonGroup<>();
    private final ProgressBar progressBar = new ProgressBar();

    /**
     * All Schedulers here!
     */
    private final HorizontalLayout rowSchedulers = new HorizontalLayout();

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
    public void initLayout() {
        syncComboBox.focus();

        syncComboBox.setLabel("Sync frecuency");
        asyncComboBoxWithCompletableFuture.setLabel("Async frecuency with CompletableFuture");
        reactiveComboBox.setLabel("Reactive frecuency with Project reactor");
        asyncComboBoxWithCompletableFuture.setWidthFull();
        syncComboBox.setItems(EnumSizeForRandomNumbers.values());
        reactiveComboBox.setItems(EnumSizeForRandomNumbers.values());
        radioButtonGroup.setItems(AllReactorSchedulersAndVirtualThreads.values());
        radioButtonGroup.setValue(AllReactorSchedulersAndVirtualThreads.BOUNDED_ELASTIC);
        radioButtonGroup.setRenderer(AllReactorSchedulersAndVirtualThreads.getIconRenderer());
        radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        asyncComboBoxWithCompletableFuture.setItems(EnumSizeForRandomNumbers.values());
        Stream.of(syncComboBox, reactiveComboBox, asyncComboBoxWithCompletableFuture)
                .forEach(e -> {
                    e.setPlaceholder("Choose stream size");
                    e.setWidth("50%");
                    e.setClearButtonVisible(true);
                    e.setItemLabelGenerator(EnumSizeForRandomNumbers::getItemLabel);
                });
        this.initSyncFrecuency();
        this.rowSchedulers.add(radioButtonGroup);
        this.rowSchedulers.setWidthFull();
        final VerticalLayout verticalLayout = new VerticalLayout(reactiveComboBox, this.rowSchedulers);
        reactiveComboBox.setWidthFull();
        //Clear reactive combo
        verticalLayout.setWidth("50%");
        verticalLayout.getStyle().set("border", "2px solid #e9ebef");
        verticalLayout.getStyle().set("border-radius", "10px");
        progressBar.setVisible(false);
        progressBar.setWidth("50%");
        progressBar.setIndeterminate(true);
        this.add(syncComboBox, asyncComboBoxWithCompletableFuture, verticalLayout, progressBar);
    }

    private void initSyncFrecuency() {
        syncComboBox.addValueChangeListener(event -> {
            if (noItemHasBeenSelected(event)) {
                try {
                    final var map = event.getValue();
                    this.execute(map.getSize(), e -> {
                        final var result = syncRandomNumbers.syncFrencuency(map.getSize());
                        this.showLogger(log, result);
                        return result;
                    });
                } catch (Exception ex) {
                    this.showError(ex.getMessage());
                }
            }
        });
    }

    private void initWithCompletableFuture(final UI ui) {
        asyncComboBoxWithCompletableFuture.addValueChangeListener(event -> {
            if (noItemHasBeenSelected(event)) {
                progressBar.setVisible(true);
                Executor executor = Executors.newFixedThreadPool(100);
                CompletableFuture.supplyAsync(() -> this.syncRandomNumbers.syncFrencuency(event.getValue().getSize())
                                , executor)
                        .whenCompleteAsync(this.action(ui, event), executor);
            }
        });
    }

    private BiConsumer<Map<Integer, Long>, Throwable> action(UI ui,
                                                          ComponentValueChangeEvent<ComboBox<EnumSizeForRandomNumbers>, EnumSizeForRandomNumbers> event) {
        return (map, error) -> {
            if (map != null) {
                ui.access(() -> {
                    this.showLogger(log, map);
                    this.execute(event.getValue().getSize(), e -> map);
                    progressBar.setVisible(false);
                });
            } else {
                ui.access(() -> {
                    this.showError(error.getMessage());
                    progressBar.setVisible(false);
                });
            }
        };
    }


    private void initReactiveFrecuency(final UI ui) {
        reactiveComboBox.addValueChangeListener(event -> {
            if (noItemHasBeenSelected(event)) {
                progressBar.setVisible(true);
                Mono.fromSupplier(() -> this.reactiveRandomNumbers.monoFrecuency(event.getValue().getSize()))
                        .subscribeOn(this.radioButtonGroup.getValue().getName())
                        .flatMap(Function.identity())
                        .doOnError(error -> ui.access(() -> {
                            this.showError(error.getMessage());
                            this.progressBar.setVisible(false);
                        }))
                        .doOnNext(onNext -> log.info("Thread name doOnNext(): {}", Thread.currentThread().getName()))
                        .subscribe(subscribeMap -> {
                            ui.access(() -> {
                                this.showLogger(log, subscribeMap);
                                log.info("Thread name subscribe(): {}", Thread.currentThread().getName());
                                this.execute(event.getValue().getSize(), e -> subscribeMap);
                                progressBar.setVisible(false);
                            });
                        });
            }
        });
    }

    private boolean noItemHasBeenSelected(ComponentValueChangeEvent<ComboBox<EnumSizeForRandomNumbers>, EnumSizeForRandomNumbers> event) {
        return event.getValue() != null;
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
