package in.juspay.testIntegrationApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class SettingsActivity extends AppCompatActivity {


    private List<String> actions, languages, mandateOptions;

    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.customerMobile)
    EditText mobileNumber;
    @BindView(R.id.customerEmail)
    EditText emailAddress;
    @BindView(R.id.customerId)
    EditText customerId;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.languageSpinner)
    Spinner languageSpinner;

    @BindView(R.id.mandateSpinner)
    Spinner mandateSpinner;
    @BindView(R.id.mandateMaxAmount)
    EditText mandateMaxAmount;

    @BindView(R.id.merchantId)
    EditText merchantId;
    @BindView(R.id.clientId)
    EditText clientId;
    @BindView(R.id.service)
    EditText service;
    @BindView(R.id.merchantKeyId)
    EditText merchantKeyId;
    @BindView(R.id.signatureURL)
    EditText signatureURL;
    @BindView(R.id.apiKey)
    EditText apiKey;
    @BindView(R.id.actionSpinner)
    Spinner actionSpinner;
    @BindView(R.id.sandbox)
    RadioButton sandbox;
    @BindView(R.id.prod)
    RadioButton prod;
    @BindView(R.id.betaAssets)
    SwitchMaterial betaAssets;


    private boolean hasChanged;
    private int languageSelected, actionSelected, mandateSelected;
    private String env;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences(PayloadConstants.SHARED_PREF_KEY, MODE_PRIVATE);
        prepareUI();
    }

    private void prepareUI() {
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle(UiUtils.getWhiteText("Configuration"));
        }

        actions = Arrays.asList("paymentPage", "paymentManagement", "addCard", "addAndLinkWallet", "nb", "upi", "delinkWallet", "quickPay", "emi");
        languages = Arrays.asList("English", "Hindi", "Tamil", "Malayalam", "Gujarati", "Marathi", "Telugu", "Bengali", "Kannada");
        mandateOptions = Arrays.asList("None", "OPTIONAL", "REQUIRED");

        ArrayAdapter<String> widgetAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, actions);
        widgetAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(widgetAdaptor);

        ArrayAdapter<String> languageAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        languageAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdaptor);

        ArrayAdapter<String> mandateAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mandateOptions);
        mandateAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mandateSpinner.setAdapter(mandateAdaptor);

        firstName.setText(preferences.getString("firstName", PayloadConstants.firstName));
        lastName.setText(preferences.getString("lastName", PayloadConstants.lastName));
        mobileNumber.setText(preferences.getString("mobileNumber", PayloadConstants.mobileNumber));
        emailAddress.setText(preferences.getString("emailAddress", PayloadConstants.emailAddress));
        customerId.setText(preferences.getString("customerId", PayloadConstants.customerId));
        amount.setText(preferences.getString("amount", PayloadConstants.amount));
        languageSelected = languages.indexOf(preferences.getString("language", PayloadConstants.language));
        if (languageSelected != -1) {
            languageSpinner.setSelection(languageSelected);
        }

        mandateSelected = mandateOptions.indexOf(preferences.getString("mandateOption", PayloadConstants.mandateOption));
        if (mandateSelected != -1) {
            mandateSpinner.setSelection(mandateSelected);
        }
        mandateMaxAmount.setText(preferences.getString("mandateMaxAmount", PayloadConstants.mandateMaxAmount));

        merchantId.setText(preferences.getString("merchantId", PayloadConstants.merchantId));
        clientId.setText(preferences.getString("clientId", PayloadConstants.clientId));
        service.setText(preferences.getString("service", PayloadConstants.service));
        merchantKeyId.setText(preferences.getString("merchantKeyId", PayloadConstants.merchantKeyId));
        signatureURL.setText(preferences.getString("signatureURL", PayloadConstants.signatureURL));
        apiKey.setText(preferences.getString("apiKey", PayloadConstants.apiKey));
        actionSelected = actions.indexOf(preferences.getString("action", PayloadConstants.processAction));
        if (actionSelected != -1) {
            actionSpinner.setSelection(actionSelected);
        }
        env = preferences.getString("environment", PayloadConstants.environment);
        if (env.equals("sandbox")) {
            sandbox.setChecked(true);
        } else {
            prod.setChecked(true);
        }
        betaAssets.setChecked(preferences.getBoolean("betaAssets", PayloadConstants.betaAssets));

        hasChanged = false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("changed", hasChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnTextChanged(R.id.firstName)
    protected void firstNameChanged(CharSequence firstName) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firstName", firstName.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.lastName)
    protected void lastNameChanged(CharSequence lastName) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastName", lastName.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.customerMobile)
    protected void mobileNumberChanged(CharSequence mobileNumber) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mobileNumber", mobileNumber.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.customerEmail)
    protected void emailAddressChanged(CharSequence emailAddress) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("emailAddress", emailAddress.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.customerId)
    protected void customerIdChanged(CharSequence customerId) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("customerId", customerId.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.amount)
    protected void amountChanged(CharSequence amount) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("amount", amount.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.mandateMaxAmount)
    protected void mandateMaxAmountChanged(CharSequence mandateMaxAmount) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mandateMaxAmount", mandateMaxAmount.toString());
        editor.apply();
    }

    @OnItemSelected(R.id.languageSpinner)
    protected void languageChanged(int position) {
        if (position != languageSelected) {
            hasChanged = true;
            String lang = languages.get(position);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", lang);
            editor.apply();
        }
    }

    @OnItemSelected(R.id.mandateSpinner)
    protected void mandateChanged(int position) {
        if (position != mandateSelected) {
            hasChanged = true;
            String lang = mandateOptions.get(position);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("mandateOption", lang);
            editor.apply();
        }
    }

    @OnTextChanged(R.id.merchantId)
    protected void merchantIdChanged(CharSequence merchantId) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("merchantId", merchantId.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.clientId)
    protected void clientIdChanged(CharSequence clientId) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("clientId", clientId.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.service)
    protected void serviceChanged(CharSequence service) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("service", service.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.merchantKeyId)
    protected void merchantKeyIdChanged(CharSequence merchantKeyId) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("merchantKeyId", merchantKeyId.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.signatureURL)
    protected void signatureURLChanged(CharSequence signatureURL) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("signatureURL", signatureURL.toString());
        editor.apply();
    }

    @OnTextChanged(R.id.apiKey)
    protected void apiKeyChanged(CharSequence apiKey) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("apiKey", apiKey.toString());
        editor.apply();
    }

    @OnItemSelected(R.id.actionSpinner)
    protected void actionChanged(int position) {
        if (position != actionSelected) {
            hasChanged = true;
            String action = actions.get(position);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("action", action);
            editor.apply();
        }
    }

    @OnCheckedChanged(R.id.sandbox)
    protected void onSandboxSelected(boolean checked) {
        if (!env.equals("sandbox") && checked) {
            hasChanged = true;
            env = "sandbox";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("environment", "sandbox");
            editor.apply();
        }
    }

    @OnCheckedChanged(R.id.prod)
    protected void onProdSelected(boolean checked) {
        if (env.equals("sandbox") && checked) {
            hasChanged = true;
            env = "production";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("environment", "production");
            editor.apply();
        }
    }

    @OnCheckedChanged(R.id.betaAssets)
    protected void onBetaCheckChanged(boolean checked) {
        hasChanged = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("betaAssets", checked);
        editor.apply();
    }

}
