package com.simulation.syncvsasync.detectblockincall;

import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@DisplayName("<= Using blockhound to detect blocking calls in parallel thread =>")
@Log4j2
@ContextConfiguration(classes = {ReactiveRandomNumbers.class})
@ExtendWith(SpringExtension.class)
class UsingBlockHoundIT {

    @Autowired
    private ReactiveRandomNumbers reactiveRandomNumbers;

    @BeforeEach
    void setup() {
        BlockHound.install();
    }

    @Test
    @DisplayName("Blocking call! in line 37")
    void detectBlockingCall1() {
        StepVerifier.create(Mono.just(1)
                .doOnNext(e -> this.blockMe())
                .subscribeOn(Schedulers.parallel()))
                .expectErrorMatches(error -> error.getMessage().contains("Blocking call!"))
                .verify();
    }

    @Test
    @DisplayName("Blocking call! in line 46")
    void detectBlockingCall2() {
        StepVerifier.create(this.reactiveRandomNumbers.monoWithBlockingCallInside(500L)
                        .subscribeOn(Schedulers.parallel()))
                .expectErrorMatches(error -> error.getMessage().contains("Blocking call!"))
                .verify();
    }

    @Test
    @DisplayName("Blocking call! in line 57, but using boudendElastic to avoid that")
    void avoidBlockingCall() {

        StepVerifier.create(Mono.just(1)
                        .doOnNext(e -> this.blockMe())
                        .subscribeOn(Schedulers.boundedElastic()))
                .expectNext(1)
                .verifyComplete();
    }

    @RepeatedTest(10)
    @DisplayName("Blocking call! in line 67, but using boudendElastic to avoid that")
    void avoidBlockingCall2() {

        StepVerifier.create(this.reactiveRandomNumbers.monoWithBlockingCallInside(500L)
                        .subscribeOn(Schedulers.boundedElastic()))
                .expectNextMatches(map -> map.size() == 6)
                .verifyComplete();
    }

    @SneakyThrows
    void blockMe() {
        Thread.sleep(1000);
    }
}
