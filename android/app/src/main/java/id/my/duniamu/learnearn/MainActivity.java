package id.my.duniamu.learnearn;

import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.androidbrowserhelper.trusted.LauncherActivity;

public class MainActivity extends LauncherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogTracker.i("MainActivity.onCreate()");
        super.onCreate(savedInstanceState);
        LogTracker.i("MainActivity: after super.onCreate()");
    }

    @Override
    protected void onStart() {
        LogTracker.i("MainActivity.onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        LogTracker.i("MainActivity.onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogTracker.i("MainActivity.onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogTracker.i("MainActivity.onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogTracker.i("MainActivity.onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogTracker.i("MainActivity.onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }
}
