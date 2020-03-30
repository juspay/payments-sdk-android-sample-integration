package in.juspay.testIntegrationApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.switchmaterial.SwitchMaterial;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTextChanged;

public class ConfigureActivity extends AppCompatActivity {

    @BindView(R.id.clientId)
    EditText clientId;
    @BindView(R.id.betaAssets)
    SwitchMaterial betaAssets;

    private SharedPreferences preferences;
    private boolean hasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        preferences = getSharedPreferences(Payload.PayloadConstants.SHARED_PREF_KEY, MODE_PRIVATE);
        prepareUI();
    }

    private void prepareUI() {
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(UiUtils.getWhiteText("Configuration"));
        }

        clientId.setText(preferences.getString("clientIdPrefetch", Payload.PayloadConstants.clientId));
        betaAssets.setChecked(preferences.getBoolean("betaAssetsPrefetch", Payload.PayloadConstants.betaAssets));

        hasChanged = false;
    }

    @OnTextChanged(R.id.clientId)
    protected void clientIdChanged(CharSequence clientId) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("clientIdPrefetch", clientId.toString());
        editor.apply();
    }

    @OnCheckedChanged(R.id.betaAssets)
    protected void onBetaCheckChanged(boolean checked) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("betaAssetsPrefetch", checked);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("changed", hasChanged);
        setResult(RESULT_OK, intent);
        finish();
    }
}
