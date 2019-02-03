package ru.hse.tpc.domain;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestTest {

    @Test
    public void test1() {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.execute(() -> System.out.println(Thread.currentThread().getId()));
        }
    }

    @Test
    public void test2() {
        ConcurrentMap<Integer, Integer> m = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            Integer n = i;
            executorService.execute(() -> {
                Integer integer = m.putIfAbsent(1, n);
                if (integer != null) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " tried to insert value " + n + " but entry exists already");
                } else {
                    System.out.println("Thread " + Thread.currentThread().getId() + " made FIRST insertion of " + n);
                }
            });
        }
        System.out.println("RESULT: " + m.get(1));
    }
}
