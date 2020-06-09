package in.juspay.testIntegrationApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(UiUtils.getWhiteText("Integration Type"));
        }

        WebView.setWebContentsDebuggingEnabled(true);
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

    public void startV1Activity(View view){
        Intent initiateIntent = new Intent(this, PaymentsActivityV1.class);
        startActivity(initiateIntent);
    }

    public void startV2Activity(View view){
        Intent initiateIntent = new Intent(this, PaymentsActivityV2.class);
        startActivity(initiateIntent);
    }
}
