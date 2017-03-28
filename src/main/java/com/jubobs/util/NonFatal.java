package com.jubobs.util;

public class NonFatal {

    private NonFatal() {}

    // not meant for static import, read it with Yoda's voice
    public boolean is(Throwable e) {
        return !(e instanceof VirtualMachineError ||
                e instanceof ThreadDeath ||
                e instanceof LinkageError);
    }
}
