package ir.anjoman.zeroone.khodroyar_co_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private final String MAIN_URL = "https://khodroyar-co.ir";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient());

        // اضافه کردن رابط برای استفاده در HTML
        webview.addJavascriptInterface(new WebAppInterface(), "Android");

        loadSiteOrOffline();
    }

    // متد چک اینترنت و بارگذاری
    private void loadSiteOrOffline() {
        if (isNetworkAvailable()) {
            webview.loadUrl(MAIN_URL);
        } else {
            webview.loadUrl("file:///android_asset/offline.html");
        }
    }

    // کلاس برای ارتباط HTML ↔ اندروید
    public class WebAppInterface {
        @JavascriptInterface
        public void retry() {
            runOnUiThread(() -> {
                if (isNetworkAvailable()) {
                    Toast.makeText(MainActivity.this, "اتصال برقرار شد ✅", Toast.LENGTH_SHORT).show();
                    webview.loadUrl(MAIN_URL);
                } else {
                    Toast.makeText(MainActivity.this, "هنوز اینترنت قطع است ❌", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager =
                (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
