package id.my.duniamu.learnearn;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * YadStore LogTracker — Canggih, ringan, thread-safe
 * Simpan log di: /Android/data/id.my.duniamu.learnearn/files/logs/
 */
public class LogTracker {

    private static final String TAG = "LearnEarn";
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PREFIX = "log_";
    private static final String LOG_FILE_EXT = ".txt";
    private static final int MAX_FILES = 5;
    private static final long MAX_FILE_SIZE = 512 * 1024; // 512 KB
    private static final Object LOCK = new Object();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    private static Context appContext = null;

    // ===== INIT =====
    public static void init(Context ctx) {
        appContext = ctx.getApplicationContext();
        log("INFO", "=== APP STARTED ===");
        log("INFO", "Device: " + Build.MANUFACTURER + " " + Build.MODEL);
        log("INFO", "Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        log("INFO", "Brand: " + Build.BRAND);
        log("INFO", "Product: " + Build.PRODUCT);
        try {
            PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
            log("INFO", "App version: " + pInfo.versionName + " (" + pInfo.versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) { /* ignore */ }
        log("INFO", "===================");
    }

    // ===== LOG HELPER =====
    public static void log(String level, String msg) {
        if (appContext == null) return;

        // Log ke Logcat juga (biar bisa debug via USB)
        if ("ERROR".equals(level) || "FATAL".equals(level)) {
            Log.e(TAG, msg);
        } else if ("WARN".equals(level)) {
            Log.w(TAG, msg);
        } else {
            Log.i(TAG, msg);
        }

        // Simpan ke file (background thread biar tidak lag)
        final String finalMsg = msg;
        final String finalLevel = level;
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeToFile(finalLevel, finalMsg);
            }
        }).start();
    }

    public static void i(String msg) { log("INFO", msg); }
    public static void w(String msg) { log("WARN", msg); }
    public static void e(String msg) { log("ERROR", msg); }
    public static void f(String msg) { log("FATAL", msg); }

    // ===== WRITE TO FILE =====
    private static void writeToFile(String level, String msg) {
        synchronized (LOCK) {
            try {
                File dir = getLogDir();
                if (dir == null) return;
                if (!dir.exists()) dir.mkdirs();

                File logFile = getCurrentLogFile(dir);
                if (logFile.length() > MAX_FILE_SIZE) {
                    rotate(dir);
                    logFile = getCurrentLogFile(dir);
                }

                String timestamp = SDF.format(new Date());
                String threadName = Thread.currentThread().getName();
                String line = "[" + timestamp + "] [" + level + "] [" + threadName + "] " + msg + "\n";

                FileWriter fw = new FileWriter(logFile, true);
                fw.write(line);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.e(TAG, "writeToFile error: " + e.getMessage());
            }
        }
    }

    // ===== GET LOG DIRECTORY =====
    public static File getLogDir() {
        if (appContext == null) return null;
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4+: /Android/data/<package>/files/logs/
            dir = new File(appContext.getExternalFilesDir(null), LOG_DIR);
        }
        if (dir == null) {
            // Fallback: internal storage
            dir = new File(appContext.getFilesDir(), LOG_DIR);
        }
        return dir;
    }

    private static File getCurrentLogFile(File dir) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        return new File(dir, LOG_FILE_PREFIX + dateStr + LOG_FILE_EXT);
    }

    // ===== ROTATE FILES =====
    private static void rotate(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        // Hapus file terlama kalau sudah lebih dari MAX_FILES
        if (files.length >= MAX_FILES) {
            File oldest = null;
            for (File f : files) {
                if (oldest == null || f.lastModified() < oldest.lastModified()) {
                    oldest = f;
                }
            }
            if (oldest != null) oldest.delete();
        }
    }

    // ===== GET ALL LOG FILES (untuk export) =====
    public static File[] getAllLogFiles() {
        File dir = getLogDir();
        if (dir == null || !dir.exists()) return new File[0];
        File[] files = dir.listFiles();
        return files != null ? files : new File[0];
    }

    // ===== CLEAR ALL LOGS =====
    public static void clear() {
        File dir = getLogDir();
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) f.delete();
        log("INFO", "Logs cleared");
    }

    // ===== EXPORT ALL LOGS AS STRING =====
    public static String exportAll() {
        File dir = getLogDir();
        if (dir == null || !dir.exists()) return "(no logs)";
        File[] files = dir.listFiles();
        if (files == null) return "(no logs)";

        StringBuilder sb = new StringBuilder();
        // Sort by date (newest first)
        java.util.Arrays.sort(files, new java.util.Comparator<File>() {
            public int compare(File a, File b) {
                return Long.compare(b.lastModified(), a.lastModified());
            }
        });
        for (File f : files) {
            sb.append("\n===== ").append(f.getName()).append(" =====\n");
            try {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
            } catch (IOException e) {
                sb.append("(read error: ").append(e.getMessage()).append(")\n");
            }
        }
        return sb.toString();
    }

    // ===== GET LOG PATH (untuk tampilkan ke user) =====
    public static String getLogPathString() {
        File dir = getLogDir();
        return dir != null ? dir.getAbsolutePath() : "(unknown)";
    }
}
