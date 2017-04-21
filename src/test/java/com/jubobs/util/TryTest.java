package com.jubobs.util;

import org.junit.Assert;
import org.junit.Test;

public class TryTest {

    @Test
    public void testFails() {
        Try<Integer> tryInt = Try
                .fallible(() -> Integer.parseInt("foo"))
                .map(i -> i + 3);

        Assert.assertTrue(tryInt.isFailure());
    }

    @Test
    public void testSucceeds() {
        Try<Integer> tryInt = Try
                .fallible(() -> Integer.parseInt("123"))
                .map(i -> i + 3);

        Assert.assertEquals(tryInt, Try.success(126));
    }

}