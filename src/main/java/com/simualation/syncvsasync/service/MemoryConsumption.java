package com.simualation.syncvsasync.service;

import org.springframework.stereotype.Service;

/**
 * @author rubn
 */
@Service
public class MemoryConsumption {

    public String getTotalMemory() {
        return String.format("Memory consumption %dMB",
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024));

    }
}
