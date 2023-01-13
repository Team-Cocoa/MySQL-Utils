package kr.teamcocoa.mysql.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SyncDetector {

    private static Thread primaryThread;

    public static void registerPrimaryThread() {
        if(primaryThread != null) {
            return;
        }
        primaryThread = Thread.currentThread();
    }

    public static void catchSynchronousCall() {
        if(Thread.currentThread() == primaryThread) {
            throw new IllegalStateException("[MySQL] Synchronous Called!");
        }
    }

}
