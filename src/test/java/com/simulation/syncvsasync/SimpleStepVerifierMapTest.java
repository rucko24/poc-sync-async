package com.simulation.syncvsasync;

import com.simulation.syncvsasync.service.SyncRandomNumbers;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Simple test for map with StepVerifier
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class SimpleStepVerifierMapTest {

    @Test
    void verifyMap() {

        final Map<Integer,Long> map = new HashMap<>();
        map.put(1,1313L);
        map.put(2,2313L);
        map.put(3,3313L);
        map.put(4,4313L);

        final Predicate<Map<Integer,Long>> predicate = e -> map.containsValue(4313L);

        StepVerifier.create(Flux.just(map)
                .log())
                .expectNextMatches(predicate)
                .verifyComplete();

    }

    @RepeatedTest(20)
    @DisplayName("Con delay y Schedulers, el de menor delay sera el publisher mas rapido" +
            "En este caso el primero es el mas rapido")
    void testConFlux() {
        Flux<Integer> flux = Flux.defer(() -> Flux.just(1, 2)
                .delayElements(Duration.ofMillis(2))
                .doOnNext(e -> log.info("Flux 1: " + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.boundedElastic()))
                .mergeWith(Flux.defer(() -> Flux.just(3, 4)
                        .delayElements(Duration.ofMillis(200))
                        .doOnNext(e -> log.info("Flux 2: " + Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.boundedElastic())));

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @RepeatedTest(1)
    @DisplayName("Con delay y Schedulers, el de menor delay sera el publisher mas rapido" +
            "En este caso el primero es el mas rapido, se debe simular el tiempo total 18 segundos" +
            "para resetear el Clock interno del Scheduler")
    void testConFluxMasTiempoVirtual() {

        Flux<Integer> flux = Flux.defer(() -> Flux.just(1, 2)
                        .delayElements(Duration.ofSeconds(4))// cada 4 segundos por item, 8 segundos en total
                        .doOnNext(e -> log.info("Flux 1: " + Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.boundedElastic()))
                .mergeWith(Flux.defer(() -> Flux.just(3, 4)
                        .delayElements(Duration.ofSeconds(5)) // cada 5 segundos por item 10 segundos en total
                        .doOnNext(e -> log.info("Flux 2: " + Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.boundedElastic())));

        // si se usa StepVerifier.create se debera esperar un tiempo unos 10 segundos aprox
        StepVerifier.withVirtualTime(() -> flux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(18))
                .expectNext(1, 3)
                .expectNext( 2, 4)
                .verifyComplete();

    }

    @RepeatedTest(10)
    @DisplayName("Sin delay, y sin schedulers, todo se ejecuta en el main-thread")
    void testConFlux2() {
       Flux<Integer> flux = Flux.defer(() -> Flux.just(1, 2)
                .doOnNext(e -> log.info("Flux 1: " + Thread.currentThread().getName()))
                .mergeWith(Flux.defer(() -> Flux.just(3, 4)
                        .doOnNext(e -> log.info("Flux 2: " + Thread.currentThread().getName()))
                )));

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @RepeatedTest(10)
    @Disabled
    @DisplayName("Con defer, y Scheduler, todo se ejecuta en el otro hilo boundedElastic" +
            " el orden de cada publisher y ejecucion es impredecible, este test fallara casi siempre.")
    void testConFlux3() {
        StepVerifier.create(Flux.defer(() -> Flux.just(1, 2)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(e -> log.info("Flux 1: " + Thread.currentThread().getName()))
                .mergeWith(Flux.defer(() -> Flux.just(3, 4)
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnNext(e -> log.info("Flux 2: " + Thread.currentThread().getName()))
                ))))
                .expectNext(1, 2, 3, 4)
                .verifyComplete();

    }

    @RepeatedTest(10)
    @Disabled
    @DisplayName("Con defer, y Scheduler, todo se ejecuta en el otro hilo boundedElastic" +
            " el orden de cada publisher y ejecucion es impredecible, este test fallara casi siempre.")
    void testConFlux4() {
        StepVerifier.create(Flux.just(1, 2)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(e -> log.info("Flux 1: " + Thread.currentThread().getName()))
                .mergeWith(Flux.just(3, 4)
                        .subscribeOn(Schedulers.boundedElastic())
                        .doOnNext(e -> log.info("Flux 2: " + Thread.currentThread().getName()))
                ))
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @RepeatedTest(10)
    @DisplayName("Sin delay, y sin schedulers, todo se ejecuta en el main-thread, por lo tanto sincrono")
    void testConMono() {
        Flux<Integer> flux = Mono.just(1)
                .map(e -> {
                    log.info("Mono map: " + Thread.currentThread().getName());
                    return e;
                })
                .doOnNext(e -> log.info("Mono 1: " + Thread.currentThread().getName()))
                .mergeWith(Mono.just(2)
                        .doOnNext(e -> log.info("Mono 2: " + Thread.currentThread().getName()))
                );

        StepVerifier.create(flux)
                .expectNext(1, 2)
                .verifyComplete();

    }

}
