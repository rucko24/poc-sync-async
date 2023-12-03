package com.simulation.syncvsasync;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

/**
 * The entry point of the Spring Boot application.
 */
@PWA(name = "Demo sync - async - reactive", shortName = "sync-async-reactive")
@SpringBootApplication
@Theme(value = "demo-sync-vs-async")
@Push
public class Application  implements AppShellConfigurator {

    public static void main(String... args) {
        BlockHound.install();
        SpringApplication.run(Application.class, args);
    }

}
