package in.juspay.testIntegrationApp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    private static final String BASE_URL = "https://hyperwidget-ppconfig.herokuapp.com/faq/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String path = getIntent().getStringExtra("path");
        String url = BASE_URL + path;

        WebView.setWebContentsDebuggingEnabled(true);

        Objects.requireNonNull(getSupportActionBar()).setTitle(UiUtils.getWhiteText("FAQ"));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
