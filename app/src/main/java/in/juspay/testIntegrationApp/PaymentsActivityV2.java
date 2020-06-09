package in.juspay.testIntegrationApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import in.juspay.hypersdk.data.JuspayResponseHandler;
import in.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter;
import in.juspay.services.HyperServices;

public class PaymentsActivityV2 extends AppCompatActivity {

    private static final int SETTINGS_ACTIVITY_REQ_CODE = 420;
    private SharedPreferences preferences;
    private ProgressDialog pd;

    // Variables for initiate
    private JSONObject signaturePayload;
    private JSONObject initiatePayload;

    private String initiateSignature;

    private boolean isSignaturePayloadSigned;
    private boolean isInitiateDone;
    private JSONObject initiateResult;

    private LinearLayout initiateLayout;


    // Variables for process
    private JSONObject orderDetails;
    private JSONObject processPayload;


    private String orderId;
    private String processSignature;

    private boolean isOrderIDGenerated;
    private boolean isOrderDetailsSigned;
    private boolean isProcessDone;
    private JSONObject processResult;

    private LinearLayout processLayout;

    // Payment services
    private HyperServices hyperServices;
    private String requestId;
    private String signURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_v2);

        createPD();
        WebView.setWebContentsDebuggingEnabled(true);

        preferences = getSharedPreferences(PayloadConstants.SHARED_PREF_KEY, MODE_PRIVATE);

        prepareUI();
        initializeParams();

        hyperServices = new HyperServices(this, findViewById(android.R.id.content));
    }

    private void prepareUI() {
        initiateLayout = findViewById(R.id.initiateLayout);
        processLayout = findViewById(R.id.processLayout);

        initiateLayout.setVisibility(View.VISIBLE);
        processLayout.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(UiUtils.getWhiteText("Initiate"));
        }
    }

    private void createPD() {
        pd = new ProgressDialog(this);
        pd.setMessage("Processing...");
        pd.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(this);
    }

    private void showPD() {
        if (pd == null) createPD();
        pd.show();
    }

    private void hidePD() {
        if (pd == null) return;
        pd.cancel();
    }

    private void initializeParams() {
        requestId = Payload.generateRequestId();
        signURL = preferences.getString("signatureURL", PayloadConstants.signatureURL);

        generateSignaturePayload();

        isSignaturePayloadSigned = false;
        initiateSignature = "";
        isInitiateDone = false;
        initiateResult = new JSONObject();

        isOrderIDGenerated = false;
        orderId = "";
        isOrderDetailsSigned = false;
        processSignature = "";
        isProcessDone = false;
        processResult = new JSONObject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Initiate Functions

    public void generateSignaturePayload() {
        signaturePayload = Payload.generateSignaturePayload(preferences);
    }

    public void generateInitiatePayload() {
        initiatePayload = Payload.generateInitiatePayloadV2(preferences, signaturePayload, initiateSignature);
    }

    public void signSignaturePayload(View view) {
        new Thread(() -> {
            try {
                SignatureAPI signatureAPI = new SignatureAPI();
                String payload;
                try {
                    payload = URLEncoder.encode(signaturePayload.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    payload = signaturePayload.toString();
                }
                initiateSignature = signatureAPI.execute(signURL, payload).get();
                isSignaturePayloadSigned = true;
                generateInitiatePayload();
                runOnUiThread(() -> Snackbar.make(view, "Payload signed", Snackbar.LENGTH_SHORT).show());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showInitiateSigningInput(View view) {
        try {
            UiUtils.showMessageInModal(this, "Signing Input", signaturePayload.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showInitiateSigningOutput(View view) {
        if (isSignaturePayloadSigned) {
            UiUtils.showMessageInModal(this, "Signing Output", initiateSignature);
        } else {
            Snackbar.make(view, "Please sign to see the output", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showSigningFAQ(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UiUtils.launchInCustomTab(this, "signing");
        } else {
            UiUtils.openWebView(this, "signing");
        }
    }

    public void initiateJuspaySdk(View view) {
        try {
            if (isSignaturePayloadSigned) {
                JSONObject payload = Payload.getPaymentsPayload(preferences, requestId, initiatePayload);
                hyperServices.initiate(payload, new HyperPaymentsCallbackAdapter() {
                    @Override
                    public void onEvent(JSONObject data, JuspayResponseHandler juspayResponseHandler) {
                        Log.d("Inside OnEvent ", "initiate");
                        try {
                            String event = data.getString("event");
                            switch (event) {
                                case "initiate_result":
                                    isInitiateDone = true;
                                    initiateResult = data;
                                    Snackbar.make(view, "Initiate Complete", Snackbar.LENGTH_SHORT).show();
                                    Log.wtf("initiate_result", data.toString());
                                    break;
                                case "process_result":
                                    isProcessDone = true;
                                    processResult = data;
                                    Objects.requireNonNull(getSupportActionBar()).show();
                                    processLayout.setVisibility(View.VISIBLE);
                                    Snackbar.make(view, "Process Complete", Snackbar.LENGTH_SHORT).show();
                                    Log.wtf("process_result", data.toString());
                                    break;
                                case "hide_loader":
                                    hidePD();
                                    break;
                                default:
                                    Snackbar.make(view, "Unknown Result", Snackbar.LENGTH_SHORT).show();
                                    UiUtils.showMessageInModal(PaymentsActivityV2.this, "Unknown Result", data.toString());
                                    Log.wtf(event, data.toString());
                                    break;
                            }
                        } catch (Exception e) {
                            Log.d("Came here", e.toString());
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Snackbar.make(view, "Please sign the payload", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInitiateInput(View view) {
        try {
            if (isSignaturePayloadSigned) {
                JSONObject payload = Payload.getPaymentsPayload(preferences, requestId, initiatePayload);
                UiUtils.showMessageInModal(this, "Initiate Input", payload.toString(4));
            } else {
                Snackbar.make(view, "Sign the payload first", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showInitiateOutput(View view) {
        try {
            if (isInitiateDone) {
                UiUtils.showMessageInModal(this, "Initiate Result", initiateResult.toString(4));
            } else {
                Snackbar.make(view, "Initiate not completed yet!", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startProcessActivity(View view) {
        if (isInitiateDone) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(UiUtils.getWhiteText("Process"));
            initiateLayout.setVisibility(View.GONE);
            processLayout.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(view, "Please Complete Initiate", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void showInitiateFAQ(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UiUtils.launchInCustomTab(this, "initiate");
        } else {
            UiUtils.openWebView(this, "initiate");
        }
    }

    // Process Functions

    public void generateOrderID(View view) {
        orderId = Payload.generateOrderId();
        isOrderIDGenerated = true;
        Snackbar.make(view, "Order ID Generated: " + orderId, Snackbar.LENGTH_SHORT).show();
        generateOrderDetails();
    }

    public void copyOrderID(View view) {
        if (isOrderIDGenerated) {
            UiUtils.copyToClipBoard(this, "OrderID", orderId);
            Snackbar.make(view, "OrderID copied to clipboard: " + orderId, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "Generate an order id", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showOrderIdFAQ(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UiUtils.launchInCustomTab(this, "orderID");
        } else {
            UiUtils.openWebView(this, "orderID");
        }
    }

    public void generateOrderDetails() {
        orderDetails = Payload.generateOrderDetails(preferences, orderId);
    }

    public void signOrderDetails(View view) {
        if (isOrderIDGenerated) {
            new Thread(() -> {
                try {
                    SignatureAPI signatureAPI = new SignatureAPI();
                    String payload;
                    try {
                        payload = URLEncoder.encode(orderDetails.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        payload = orderDetails.toString();
                    }
                    processSignature = signatureAPI.execute(signURL, payload).get();
                    isOrderDetailsSigned = true;
                    generateProcessPayload();
                    runOnUiThread(() -> Snackbar.make(view, "Signed Order Details", Snackbar.LENGTH_SHORT).show());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Snackbar.make(view, "Please generate an order id", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showProcessSigningInput(View view) {
        try {
            if (isOrderIDGenerated) {
                UiUtils.showMessageInModal(this, "Signing Input", orderDetails.toString(4));
            } else {
                Snackbar.make(view, "Generate an order id", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showProcessSigningOutput(View view) {
        if (isOrderDetailsSigned) {
            UiUtils.showMessageInModal(this, "Signing Output", processSignature);
        } else {
            Snackbar.make(view, "Please sign to see the output", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void generateProcessPayload() {
        processPayload = Payload.generateProcessPayloadV2(preferences, orderId, orderDetails, processSignature);
    }

    public void showProcessInput(View view) {
        try {
            if (isOrderDetailsSigned) {
                JSONObject payload = Payload.getPaymentsPayload(preferences, requestId, processPayload);
                UiUtils.showMessageInModal(this, "Process Input", payload.toString(4));
            } else {
                Snackbar.make(view, "Please sign the order details first", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showProcessOutput(View view) {
        try {
            if (isProcessDone) {
                UiUtils.showMessageInModal(this, "Process Result", processResult.toString(4));
            } else {
                Snackbar.make(view, "Process not completed yet!", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processJuspaySdk(View view) {
        if (isOrderDetailsSigned) {
            showPD();
            JSONObject payload = Payload.getPaymentsPayload(preferences, requestId, processPayload);
            hyperServices.process(payload);
            Objects.requireNonNull(getSupportActionBar()).hide();
        } else {
            Snackbar.make(view, "Please sign the payload", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showProcessFAQ(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UiUtils.launchInCustomTab(this, "process");
        } else {
            UiUtils.openWebView(this, "process");
        }
    }

    private void reset() {
        prepareUI();
        initializeParams();
    }

    public void terminateJuspaySdk(View view) {
        Snackbar.make(view, "Juspay SDK terminated", Snackbar.LENGTH_SHORT).show();
        hyperServices.terminate();
        reset();
    }

    public void showTerminateFAQ(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UiUtils.launchInCustomTab(this, "terminate");
        } else {
            UiUtils.openWebView(this, "terminate");
        }
    }

    @Override
    public void onBackPressed() {
        boolean backPressHandled = hyperServices.onBackPressed();
        if (!backPressHandled) {
            if (processLayout.getVisibility() == View.VISIBLE) {
                processLayout.setVisibility(View.GONE);
                initiateLayout.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setTitle(UiUtils.getWhiteText("Initiate"));
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configure:
                Intent intent = new Intent(PaymentsActivityV2.this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_ACTIVITY_REQ_CODE, new Bundle());
                return true;
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SETTINGS_ACTIVITY_REQ_CODE) {
            if (data != null) {
                if (data.hasExtra("changed") && data.getBooleanExtra("changed", false)) {
                    Toast.makeText(this, "Resetting due to change in parameters", Toast.LENGTH_SHORT).show();
                    reset();
                    if (isInitiateDone) {
                        hyperServices.terminate();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
