package com.simualation.syncvsasync.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rubn
 */
@Service
public class SyncRandomNumbers {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int RANDOM_NUMBER_ORIGIN = 1;
    private static final int RANDOM_NUMBER_BOUND = 7;

    /**
     *
     * @param size of stream
     * @return Map<Integer,Long>
     */
    public Map<Integer, Long> syncFrencuency(Long size) {
        return SECURE_RANDOM.ints(size, RANDOM_NUMBER_ORIGIN, RANDOM_NUMBER_BOUND)
                .boxed()
                .collect(Collectors.groupingByConcurrent(e -> e, Collectors.counting()));
    }

}
