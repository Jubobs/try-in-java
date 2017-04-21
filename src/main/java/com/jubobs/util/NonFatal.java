package com.jubobs.util;

public class NonFatal {

    private NonFatal() {}

    public static boolean isNonFatal(Throwable e) {
        return !(e instanceof VirtualMachineError ||
                e instanceof ThreadDeath ||
                e instanceof LinkageError);
    }
}
