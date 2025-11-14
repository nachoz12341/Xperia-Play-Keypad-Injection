package com.nachozgames.xperiaplaykeypadinjection.Injection;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TouchpadWriter
 *  - Injects multitouch events into a Linux input event node (e.g. /dev/input/eventX)
 *  - Uses 'su' to spawn a root shell and 'cat' to write to the event node
 *  - Supports multiple contacts via touchId parameter in down/move/up methods
 *  - Call start() before using, and close() when done
 *  - Call flush() to ensure all events are sent
 */

public class KeypadZeusWriter implements Closeable {
    private static final String EVENT_NAME = "keypad-zeus";

    // Linux input constants
    private static final int EV_SYN = 0x00, EV_KEY = 0x01, EV_ABS = 0x03, EV_MSC = 0x04, EV_SW = 0x05;
    private static final int SYN_REPORT = 0, SYN_MT_REPORT = 2;
    private static final int ABS_MT_TRACKING_ID = 0x0039;
    private static final int ABS_MT_POSITION_X  = 0x0035;
    private static final int ABS_MT_POSITION_Y  = 0x0036;
    private static final int ABS_MT_TOUCH_MAJOR  = 0x0030;
    private static final int ABS_MT_TOUCH_MINOR  = 0x0031;
    private static final int ABS_MT_TOUCH_ORIENTATION  = 0x0034;

    private Process suProc;
    private BufferedOutputStream bos;
    private final byte[] frame = new byte[16]; // timeval(sec,usec)=8 + type2 + code2 + value4
    private final ByteBuffer bb = ByteBuffer.wrap(frame).order(ByteOrder.LITTLE_ENDIAN);
    private int lastX = 0, lastY = 0;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public KeypadZeusWriter() {
    }

    /** Start a single root sink: cat >/dev/input/eventX and keep its stdin open */
    public void start(String eventNodePath) throws IOException {
        if (!started.compareAndSet(false, true)) return;
        String cmd = "exec /system/bin/sh -c \"/system/bin/cat > " + eventNodePath + "\"";
        suProc = new ProcessBuilder("su", "-c", cmd).redirectErrorStream(true).start();
        bos = new BufferedOutputStream(suProc.getOutputStream(), 4096);
    }

    private void writeEvent(int type, int code, int value) throws IOException {
        long nowMs = System.currentTimeMillis();
        int sec  = (int)(nowMs / 1000L);
        int usec = (int)((nowMs % 1000L) * 1000L);

        bb.clear();
        bb.putInt(sec);
        bb.putInt(usec);
        bb.putShort((short)(type & 0xFFFF));
        bb.putShort((short)(code & 0xFFFF));
        bb.putInt(value);
        bos.write(frame, 0, 16);
    }

    private void synFrame() throws IOException {
        writeEvent(EV_SYN, SYN_REPORT, 0);
    }

    public void key(int linuxKey, int value) throws IOException {
        if (!started.get())
            throw new IllegalStateException("Call start() first");

        writeEvent(EV_KEY, linuxKey, value);
    }

    public void switchEvent(int code, int value) throws IOException {
        if (!started.get())
            throw new IllegalStateException("Call start() first");

        writeEvent(EV_SW, code, value);
        synFrame();
    }

    public void mscEvent(int code, int value) throws IOException {
        if (!started.get())
            throw new IllegalStateException("Call start() first");

        writeEvent(EV_MSC, code, value);
        synFrame();
    }

    public void flush() throws IOException {
        if (!started.get())
            return;
        synFrame();
        bos.flush();
    }

    @Override public void close() throws IOException {
        if (bos != null) try { bos.close(); } catch (IOException ignored) {}
        if (suProc != null) suProc.destroy();
        started.set(false);
    }
}
