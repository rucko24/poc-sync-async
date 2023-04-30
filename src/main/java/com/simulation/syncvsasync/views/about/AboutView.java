package com.simulation.syncvsasync.views.about;

import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.base.devserver.BrowserLauncher;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("About")
@Route(value = "about", layout = MainView.class)
public class AboutView extends VerticalLayout {

    private final Image img = new Image("images/loom.png", "placeholder plant");
    private final H2 header = new H2("This place intentionally left empty");
    private static final String TARGET_BLANK = "_blank";
    private static final String INNER_HTML = "innerHTML";
    private static final String LOOM_URL = "https://openjdk.org/projects/loom/";

    public AboutView() {
        setSizeFull();

        super.setSpacing(false);

        img.setWidth("300px");
        img.addClassName("image-border");
        img.addClickListener(imageClickEvent -> {
            UI.getCurrent().getPage().open(LOOM_URL, TARGET_BLANK);
        });
        super.add(img);

//
        super.add(sourceOfThePoc());
        //
        super.add(row());


        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private Div sourceOfThePoc() {
        final Anchor anchorLoom = new Anchor("https://github.com/rucko24/poc-sync-asyn");
        anchorLoom.getElement().setProperty(INNER_HTML, "<strong>PoC source</strong>");
        anchorLoom.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(anchorLoom);
        Tooltip.forComponent(div).setText("https://github.com/rucko24/poc-sync-asyn");
        div.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        return div;
    }

    private Div row() {
        final Div row = new Div();
        row.addClassName("row-about");
        row.getStyle().set("display", "flex");
        row.getStyle().set("align-items", "baseline");
        row.getStyle().set("justify-content", "center");
        row.getStyle().set("gap", "5px");

        final Div div = new Div();
        div.getElement().setProperty(INNER_HTML, "<p>This project internally uses </p>");
        row.add(div);
        row.add(this.projectLoomDiv());
        row.add(new Span(" and "));
        row.add(this.blockHoundDiv());
        return row;
    }

    private Div projectLoomDiv() {
        final Anchor anchorLoom = new Anchor(LOOM_URL);
        anchorLoom.getElement().setProperty(INNER_HTML, "<strong>Project Loom</strong>");
        anchorLoom.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(anchorLoom);
        Tooltip.forComponent(div).setText(LOOM_URL);
        return div;
    }

    private Div blockHoundDiv() {
        final Anchor blockHoundAnchor = new Anchor("https://github.com/reactor/BlockHound");
        Tooltip.forComponent(blockHoundAnchor).setText("https://github.com/reactor/BlockHound");
        blockHoundAnchor.getElement().setProperty(INNER_HTML, "<strong>BlockHound 🔥</strong>");
        blockHoundAnchor.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(blockHoundAnchor);
        return div;
    }

}