package com.nachozgames.xperiaplaykeypadinjection.Util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public final class Toasts {
    private static volatile Context appCtx;
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static WeakReference<Toast> last = new WeakReference<Toast>(null);

    private Toasts() {}

    /** Call once (e.g., in Application.onCreate). */
    public static void init(Context context) {
        appCtx = context.getApplicationContext();
    }

    /** Post a toast to the main thread (SHORT by default). */
    public static void show(final CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /** Post a toast to the main thread with custom duration. */
    public static void show(final CharSequence text, final int duration) {
        MAIN.post(new Runnable() {
            @Override public void run() {
                // Cancel the previous toast so they don't stack.
                Toast t = last.get();
                if (t != null) t.cancel();

                Toast nt = Toast.makeText(appCtx, text, duration);
                last = new WeakReference<Toast>(nt);
                nt.show();
            }
        });
    }

    /** Convenience for string resources with optional format args. */
    public static void show(final int resId, final Object... args) {
        MAIN.post(new Runnable() {
            @Override public void run() {
                Toast t = last.get();
                if (t != null) t.cancel();

                String msg = (args == null || args.length == 0)
                        ? appCtx.getString(resId)
                        : appCtx.getString(resId, args);
                Toast nt = Toast.makeText(appCtx, msg, Toast.LENGTH_SHORT);
                last = new WeakReference<Toast>(nt);
                nt.show();
            }
        });
    }
}
