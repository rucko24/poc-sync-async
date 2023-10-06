package com.simulation.syncvsasync;

import com.simulation.syncvsasync.service.ReactiveRandomNumbers;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.when;

/**
 * The class RandonNumbersTest
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@DisplayName("<= Mocking ReactiveRandomNumbers services =>")
class RandonNumbersTest {

    @MockBean
    private ReactiveRandomNumbers service;

    private final Map<Integer, Long> map = new ConcurrentHashMap<>();
    private static final long SIZE = 5L;

    @BeforeEach
    void setup() {
        map.put(1, 100L);
        map.put(2, 200L);

        Mono<Map<Integer, Long>> monoMap = Mono.just(map);

        when(service.monoFrecuency(SIZE)).thenReturn(monoMap);
    }

    @Test
    @DisplayName("Mocking ReactiveRandomNumbers services")
    void reactiveRandomNumber() {

        StepVerifier.create(service.monoFrecuency(SIZE)
                        .doOnNext(signal -> log.info("doOnNext() {}", signal)))
                .expectNext(map) // Map original
                .verifyComplete();
    }
}
