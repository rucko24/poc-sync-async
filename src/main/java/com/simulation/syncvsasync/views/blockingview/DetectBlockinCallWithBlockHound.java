package com.simulation.syncvsasync.views.blockingview;

import com.simulation.syncvsasync.enumsizesfornumbers.AllReactorSchedulersAndVirtualThreads;
import com.simulation.syncvsasync.enumsizesfornumbers.EnumSizeForRandomNumbers;
import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import com.simulation.syncvsasync.util.NotificationsUtils;
import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Left;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Right;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@SpringComponent
@UIScope
@Route(value = "blocking-calls", layout = MainView.class)
@PageTitle("BlockHound")
@RequiredArgsConstructor
public class DetectBlockinCallWithBlockHound extends Div implements NotificationsUtils {
    //
    private final ReactiveRandomNumbers reactiveRandomNumbers;
    private final TextArea textArea = new TextArea();
    private final VerticalLayout contentLayout = new VerticalLayout();
    private final Button clearTextArea = new Button("X", VaadinIcon.TRASH.create());
    private final ComboBox<AllReactorSchedulersAndVirtualThreads> schedulersComboBox = new ComboBox<>("Make call with a Scheduler");
    private final ProgressBar progressBar = new ProgressBar();

    @PostConstruct
    public void init() {
        this.setId("parent");
        this.setSizeFull();
        this.getStyle().set("overflow-y", "hidden");
        this.header();
        this.fillComboBoxWithSchedulersAndCustomize();
        this.customTextArea();
    }

    private void header() {
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        clearTextArea.setTooltipText("Clear output");
        clearTextArea.addThemeVariants(ButtonVariant.LUMO_ERROR);

        final var divProgressBar = new FlexLayout(progressBar);
        divProgressBar.setWidthFull();
        divProgressBar.addClassNames(Right.MEDIUM, Left.MEDIUM, AlignItems.CENTER);
        divProgressBar.setId("div-progressbar");

        clearTextArea.addClickListener(clickEvent -> {
            this.textArea.clear();
            this.schedulersComboBox.clear();
        });
        clearTextArea.addClassName(Left.MEDIUM);

        final FlexLayout header = new FlexLayout(schedulersComboBox, clearTextArea);
        header.setWidthFull();
        header.addClassName("header-view-blockhound");

        this.contentLayout.add(header, divProgressBar);
        this.contentLayout.setSizeFull();
        this.contentLayout.setMargin(false);
        this.contentLayout.setPadding(true);
        this.contentLayout.setHeightFull();
        super.add(contentLayout);
    }

    private void fillComboBoxWithSchedulersAndCustomize() {
        schedulersComboBox.setItems(AllReactorSchedulersAndVirtualThreads.values());
        schedulersComboBox.setRenderer(AllReactorSchedulersAndVirtualThreads.getIconRenderer());
        schedulersComboBox.setItemLabelGenerator(item -> {
            final var scheduler = item.getSchedulers();
            if (scheduler.equals(Schedulers.immediate())
                    || scheduler.equals(AllReactorSchedulersAndVirtualThreads.THREAD_PER_TASK_EXECUTOR.getSchedulers())
                    || scheduler.equals(AllReactorSchedulersAndVirtualThreads.VIRTUAL_THREAD_PER_TASK_EXECUTOR.getSchedulers())
                    || scheduler.equals(Schedulers.boundedElastic())) {
                return item.toString().concat(" 😁");
            }
            return item.toString().concat(" 😭");
        });
        this.schedulersComboBox.setClearButtonVisible(true);
        this.schedulersComboBox.setPlaceholder("Select Scheduler...");
        this.schedulersComboBox.addClassName("combo-schedulers");

    }

    private void customTextArea() {
        this.textArea.setLabel("Output");
        this.textArea.setTooltipText("Output");
        this.textArea.setValueChangeMode(ValueChangeMode.EAGER);
        this.textArea.setWidthFull();
        this.textArea.setHeight("80%");

        this.textArea.getStyle().set("scrollbar-width","thin");
        this.textArea.addClassName("textarea-scrollbar");
        this.contentLayout.getStyle().set("scrollbar-width","thin");
        this.contentLayout.add(textArea);

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
                        .subscribeOn(value.getValue().getSchedulers())
                        .flatMap(Function.identity())
                        //Detected error with BlockHound
                        .doOnError(error -> {
                            ui.access(() -> {
                                final Scheduler scheduler = value.getValue().getSchedulers();
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
