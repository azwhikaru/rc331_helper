package com.azwhikaru.rc331;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

final class RootCommands {
    static final String TAG = "RC331Helper";
    static final String HELPER = "/dji_apk/.helper";

    private RootCommands() {}

    static void apply() {
        if (!new File(HELPER).isFile()) {
            Log.w(TAG, "helper_missing=" + HELPER);
            return;
        }
        Result result = run(4500, HELPER);
        String message = "helper rc=" + result.code + " " + result.output;
        if (result.code == 0) Log.i(TAG, message);
        else Log.e(TAG, message);
    }

    static void rebootBootloader() {
        reboot("reboot,bootloader");
    }

    static void rebootNormal() {
        reboot("reboot");
    }

    private static void reboot(String value) {
        Result result = run(3000, "/system/bin/setprop", "sys.powerctl", value);
        if (result.code == 0 || Thread.currentThread().isInterrupted()) return;
        Log.e(TAG, "reboot rc=" + result.code + " " + result.output);
    }

    private static Result run(long timeoutMs, String... command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            if (!process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                process.waitFor(100, TimeUnit.MILLISECONDS);
                return new Result(124, "timeout");
            }
            return new Result(process.exitValue(), "");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result(130, "interrupted");
        } catch (IOException | SecurityException e) {
            return new Result(126, e.toString());
        } finally {
            if (process != null && process.isAlive()) process.destroyForcibly();
        }
    }

    private static final class Result {
        final int code;
        final String output;
        Result(int code, String output) { this.code = code; this.output = output; }
    }
}
