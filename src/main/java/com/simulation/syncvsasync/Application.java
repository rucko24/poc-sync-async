package com.simulation.syncvsasync;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import reactor.blockhound.BlockHound;

/**
 * The entry point of the Spring Boot application.
 */
@PWA(name = "Demo sync - async - reactive", shortName = "sync-async-reactive")
@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Push
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String... args) {
//        BlockHound.install();
        SpringApplication.run(Application.class, args);
    }

}
