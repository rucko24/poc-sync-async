package com.simulation.syncvsasync.views.errorview;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Error view
 */
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private Span explanation;

    public ErrorView() {
        super.setSizeFull();
        final H1 header = new H1("404 Not found!!!");
        super.add(header);

        explanation = new Span();

        final Anchor anchor = new Anchor("/", "back to home...");
        anchor.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.BOLD);

        super.add(explanation, anchor);
        super.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        return HttpServletResponse.SC_NOT_FOUND;
    }
}