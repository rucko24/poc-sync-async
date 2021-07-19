package com.simulation.syncvsasync.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rubn
 */
@Service
public class ReactiveRandomNumbers {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int RANDOM_NUMBER_ORIGIN = 1;
    private static final int RANDOM_NUMBER_BOUND = 7;

    /**
     *
     * @param size of stream
     * @return Flux<Map<Integer,Long>>
     */
    public Flux<Map<Integer, Long>> fluxFrecuency(Long size) {
        return Flux.just(SECURE_RANDOM.ints(size, RANDOM_NUMBER_ORIGIN, RANDOM_NUMBER_BOUND)
                .boxed()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting())));
    }

}
