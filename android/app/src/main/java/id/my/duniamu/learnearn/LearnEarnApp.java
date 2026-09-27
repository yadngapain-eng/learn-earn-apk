package id.my.duniamu.learnearn;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.webkit.WebView;

public class LearnEarnApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LogTracker
        LogTracker.init(this);
        LogTracker.i("Application.onCreate()");

        // Setup global exception handler
        setupCrashHandler();

        // Enable WebView debugging (debug build only)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Log config
        Configuration cfg = getResources().getConfiguration();
        LogTracker.i("Locale: " + cfg.locale);
        LogTracker.i("Orientation: " + (cfg.orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape"));
        LogTracker.i("Screen: " + cfg.screenWidthDp + "x" + cfg.screenHeightDp + "dp");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogTracker.i("onConfigurationChanged: " + newConfig.orientation);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogTracker.w("LOW MEMORY WARNING");
    }

    @Override
    public void onTerminate() {
        LogTracker.i("Application.onTerminate()");
        super.onTerminate();
    }

    // ===== CRASH HANDLER =====
    private void setupCrashHandler() {
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // Log crash
                LogTracker.f("=== UNCAUGHT EXCEPTION ===");
                LogTracker.f("Thread: " + thread.getName());
                LogTracker.f("Type: " + throwable.getClass().getName());
                LogTracker.f("Message: " + throwable.getMessage());

                // Stack trace
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                throwable.printStackTrace(pw);
                LogTracker.f("Stack trace:\n" + sw.toString());
                LogTracker.f("=========================");

                // Panggil handler default
                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, throwable);
                }
            }
        });
    }
}
