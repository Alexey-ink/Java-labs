package ru.spbstu.telematics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ThreadPool {
    public static ExecutorService create(int threads, boolean isVirtual) {
        return isVirtual ?
                Executors.newVirtualThreadPerTaskExecutor() :
                Executors.newFixedThreadPool(threads);
    }
}
