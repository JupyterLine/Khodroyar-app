package ir.anjoman.zeroone.khodroyar_co_app;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private String lastUrl = "https://khodroyar-co.ir"; // آدرس پیشفرض
    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = findViewById(R.id.webview);
        webview.setLongClickable(false);
        webview.setHapticFeedbackEnabled(false);
        webview.setOnLongClickListener(v -> true);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                lastUrl = url; // ذخیره آخرین آدرس بازشده
                super.onPageFinished(view, url);
            }
        });
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void retry() {
                runOnUiThread(() -> {
                    // بررسی دوباره اینترنت یا بارگذاری مجدد WebView
                    webview.loadUrl("https://khodroyar-co.ir");
                });
            }
        }, "Android");

        // 🔹 چک اولیه موقع ورود
        if (Utils.isNetworkAvailable(this)) {
            webview.loadUrl("file:///android_asset/offline.html");
        } else {
            Toast.makeText(this, "اینترنت در دسترس نیست ❌", Toast.LENGTH_SHORT).show();
            webview.loadUrl("file:///android_asset/offline.html");
        }

        // 🔹 مانیتور کردن وضعیت اینترنت در طول استفاده
        networkMonitor = new NetworkMonitor(this, new NetworkMonitor.NetworkListener() {
            @Override
            public void onAvailable() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "اتصال اینترنت برقرار شد ✅", Toast.LENGTH_SHORT).show();
                    webview.loadUrl("https://khodroyar-co.ir"); // برگرداندن به همان صفحه قبلی
                });
            }

            @Override
            public void onLost() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "اتصال اینترنت قطع شد ❌", Toast.LENGTH_SHORT).show();
                    webview.loadUrl("file:///android_asset/offline.html");
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkMonitor != null) {
            networkMonitor.unregister();
        }
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