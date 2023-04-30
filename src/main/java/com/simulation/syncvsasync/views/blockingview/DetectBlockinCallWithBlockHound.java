package com.simulation.syncvsasync.views.blockingview;

import com.simulation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import com.simulation.syncvsasync.service.SyncRandomNumbers;
import com.simulation.syncvsasync.util.NotificationsUtils;
import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@SpringComponent
@UIScope
@Route(value = "blocking-calls", layout = MainView.class)
@PageTitle("BlockHound")
@RequiredArgsConstructor
public class DetectBlockinCallWithBlockHound extends VerticalLayout implements NotificationsUtils {
    //
    private final ReactiveRandomNumbers reactiveRandomNumbers;
    private final SyncRandomNumbers syncRandomNumbers;
    private final TextArea textArea = new TextArea();
    private final Button clearTextArea = new Button("X", VaadinIcon.TRASH.create());
    private final ComboBox<Scheduler> schedulersComboBox = new ComboBox<>("Make call with a Scheduler");
    private final ProgressBar progressBar = new ProgressBar();
    private final HorizontalLayout header = new HorizontalLayout();

    @PostConstruct
    public void init() {
        this.setSizeFull();
        this.header();
        this.fillComboBoxWithSchedulersAndCustomize();
    }

    private void header() {
        progressBar.setWidth("10%");
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        clearTextArea.setTooltipText("Clear output");
        header.setWidthFull();
        header.add(progressBar, schedulersComboBox, clearTextArea);
        header.setAlignItems(Alignment.BASELINE);
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        clearTextArea.addClickListener(clickEvent -> this.textArea.clear());
        super.add(header);
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
                return "Schedulers.immediate() 😁";
            } else if (item.equals(Schedulers.boundedElastic())) {
                return "Schedulers.boundedElastic() 😁";
            }
            return item.toString().concat(" 😭");
        });
        this.schedulersComboBox.setClearButtonVisible(true);
        this.schedulersComboBox.setPlaceholder("Select Scheduler...");
        this.schedulersComboBox.setWidth("50%");
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        this.textArea.setSizeFull();
        super.addAndExpand(this.textArea);
    }

    /**
     * @param ui the ui for concurrency access
     */
    private void makeCall(final UI ui) {
        this.schedulersComboBox.setAllowCustomValue(false);
        this.schedulersComboBox.addValueChangeListener(value -> {
            if (Objects.nonNull(value.getValue())) {
                this.progressBar.setVisible(true);
                Mono.fromCallable(() ->
                                this.reactiveRandomNumbers.monoFrecuency(EnumSizeForRandomNumbers.TEN_MILLION.getSize()))
                        //Set a Scheduler at runtime
                        .subscribeOn(value.getValue())
                        .flatMap(Function.identity())
                        //Detected error with BlockHound
                        .doOnError(error -> {
                            ui.access(() -> {
                                final Scheduler scheduler = value.getValue();
                                this.showError(scheduler + " " + error);
                                textArea.clear();
                                String stackTrace = scheduler.toString()
                                        .concat(" ")
                                        .concat(error.toString())
                                        .concat("\n\n")
                                        .concat(Arrays.stream(error.getStackTrace())
                                                .map(String::valueOf)
                                                .collect(Collectors.joining("\n")));
                                textArea.setValue(stackTrace);
                                this.progressBar.setVisible(false);
                            });
                        })
                        .doOnEach(signal -> log.info("Thread name: {}", Thread.currentThread().getName()))
                        .subscribe(monoMap -> {
                            ui.access(() -> {
                                this.showMessage("Map: " + monoMap);
                                this.progressBar.setVisible(false);
                            });
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
            this.schedulersComboBox.focus();
        }
    }
}
