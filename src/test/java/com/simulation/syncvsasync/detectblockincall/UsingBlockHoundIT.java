package com.simulation.syncvsasync.detectblockincall;

import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Blocking call! in line 38")
    void detectBlockingCall1() {
        StepVerifier.create(Mono.just(1)
                .doOnNext(e -> this.blockMe())
                .subscribeOn(Schedulers.parallel()))
                .expectErrorMatches(error -> error.getMessage().contains("Blocking call!"))
                .verify();
    }

    @Test
    @DisplayName("Blocking call! in line 47")
    void detectBlockingCall2() {
        StepVerifier.create(this.reactiveRandomNumbers.monoWithBlockingCallInside(500L)
                        .subscribeOn(Schedulers.parallel()))
                .expectErrorMatches(error -> error.getMessage().contains("Blocking call!"))
                .verify();
    }

//    @SneakyThrows
    void blockMe() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
