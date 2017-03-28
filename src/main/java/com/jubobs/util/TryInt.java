package com.jubobs.util;

import java.util.function.IntSupplier;

public class TryInt {

    private final int value;

    private final Throwable throwable;

    private TryInt(Throwable e) {
        this.throwable = e;
        this.value = 0;
    }

    private TryInt(int value) {
        this.throwable = null;
        this.value = value;
    }

    public static TryInt fallible(IntSupplier supplier) {
        try {
            return new TryInt(supplier.getAsInt());
        } catch (Throwable e) {
            if (nonFatal(e)) {
                return new TryInt(e);
            } else {
                throw e;
            }
        }
    }


}
