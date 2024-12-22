package com.simulation.syncvsasync.views.about;

import com.simulation.syncvsasync.views.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Top;

@PageTitle("About")
@Route(value = "about", layout = MainView.class)
public class AboutView extends VerticalLayout {

    private final Image img = new Image("images/loom.png", "loom");
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
        super.add(img, sourceOfThePoc(), row());

        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

    }

    private Div sourceOfThePoc() {
        final Anchor anchorLoom = new Anchor("https://github.com/rucko24/poc-sync-asyn");
        anchorLoom.getElement().setProperty(INNER_HTML, "<strong>PoC source</strong>");
        anchorLoom.setTarget(TARGET_BLANK);
        final Div div = new Div();
        div.add(anchorLoom);
        Tooltip.forComponent(div).setText("https://github.com/rucko24/poc-sync-asyn");
        div.addClassNames(Top.MEDIUM, Margin.Bottom.MEDIUM);
        return div;
    }

    private Div row() {
        final Div row = new Div();
        row.setWidthFull();
        row.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.CENTER, JustifyContent.CENTER);
        final Div div = new Div();
        div.getElement().setProperty(INNER_HTML, "<p>This project internally uses </p>");
        row.add(div);
        row.add(this.projectLoomAndBlockHoundDiv());
        return row;
    }

    private Div projectLoomAndBlockHoundDiv() {
        final Anchor anchorLoom = new Anchor(LOOM_URL);
        anchorLoom.getElement().setProperty(INNER_HTML, "<strong>Project Loom</strong>");
        anchorLoom.setTarget(TARGET_BLANK);

        final Anchor blockHoundAnchor = new Anchor("https://github.com/reactor/BlockHound");
        Tooltip.forComponent(blockHoundAnchor).setText("https://github.com/reactor/BlockHound");
        blockHoundAnchor.getElement().setProperty(INNER_HTML, "<strong>BlockHound 🔥</strong>");
        blockHoundAnchor.setTarget(TARGET_BLANK);

        final Div div = new Div();
        div.add(anchorLoom, new Span(" and "), blockHoundAnchor);
        Tooltip.forComponent(div).setText(LOOM_URL);
        return div;
    }

}