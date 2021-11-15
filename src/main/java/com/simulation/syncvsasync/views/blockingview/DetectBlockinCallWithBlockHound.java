package com.simulation.syncvsasync.views.blockingview;

import com.simulation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import com.simulation.syncvsasync.service.SyncRandomNumbers;
import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@SpringComponent
@UIScope
@Route(value = "blocking-calls", layout = MainView.class)
@PageTitle("BlockHound")
@RequiredArgsConstructor
public class DetectBlockinCallWithBlockHound extends VerticalLayout {
    //
    private final ReactiveRandomNumbers service;
    private final SyncRandomNumbers syncRandomNumbers;
    private final ComboBox<Scheduler> schedulersComboBox = new ComboBox<>("Make call with a Scheduler");

    @PostConstruct
    public void init() {
        this.fillComboBoxWithSchedulersAndCustomize();
        super.add(schedulersComboBox);
    }

    private void fillComboBoxWithSchedulersAndCustomize() {
        final List<Scheduler> schedulersList = new CopyOnWriteArrayList<>();
        schedulersList.add(Schedulers.boundedElastic());
        schedulersList.add(Schedulers.single());
        schedulersList.add(Schedulers.immediate());
        schedulersList.add(Schedulers.parallel());
        schedulersComboBox.setItems(schedulersList);
        schedulersComboBox.setItemLabelGenerator(item -> {
            if (item.equals(Schedulers.immediate())) {
                return "Schedulers.immediate()";
            }
            return item.toString();
        });
        this.schedulersComboBox.setClearButtonVisible(true);
        this.schedulersComboBox.setPlaceholder("Select Scheduler...");
        this.schedulersComboBox.setWidth("50%");
    }

    /**
     * @param ui the ui for concurrency access
     */
    private void makeCall(final UI ui) {
        this.schedulersComboBox.setPreventInvalidInput(true);
        this.schedulersComboBox.setAllowCustomValue(false);
        this.schedulersComboBox.addValueChangeListener(value -> {
            if (Objects.nonNull(value)) {
                Mono.fromCallable(() ->
                                Mono.just(this.syncRandomNumbers.syncFrencuency(EnumSizeForRandomNumbers.TEN_MILLION.getSize())))
                        .subscribeOn(value.getValue())
                        //Detected error with BlockHound
                        .onErrorContinue((Throwable error, Object o) -> {
                            log.error("Error -> {} {}", value.getValue(), error);
                            ui.access(() -> {
                                final Notification n = new Notification(value.getValue() + " " + error);
                                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                                n.setDuration(2500);
                                n.open();
                            });
                        })
                        //Set a Scheduler at runtime
                        .doOnEach(signal -> log.info("Thread name: {}", Thread.currentThread().getName()))
                        .flatMap(mapMono -> Mono.defer(() -> mapMono))
                        .subscribe(monoMap -> {
                            ui.access(() -> Notification.show("Map: " + monoMap));
                        });

            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (attachEvent.isInitialAttach()) {
            this.makeCall(attachEvent.getUI());
        }
    }
}
