package com.simulation.syncvsasync;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Simple test for map with StepVerifier
 */
@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class SyncVsAsyncTest {

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

}
