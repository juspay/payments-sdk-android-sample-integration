package in.juspay.testIntegrationApp;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Payload {

    public static String getTimeStamp() {
        return Long.toString(System.currentTimeMillis());
    }

    public static JSONObject generateSignaturePayload(SharedPreferences preferences) {
        JSONObject signaturePayload = new JSONObject();
        try {
            signaturePayload.put("first_name", preferences.getString("firstName", PayloadConstants.firstName));
            signaturePayload.put("last_name", preferences.getString("lastName", PayloadConstants.lastName));
            signaturePayload.put("mobile_number", preferences.getString("mobileNumber", PayloadConstants.mobileNumber));
            signaturePayload.put("email_address", preferences.getString("emailAddress", PayloadConstants.emailAddress));
            signaturePayload.put("customer_id", preferences.getString("customerId", PayloadConstants.customerId));
            signaturePayload.put("timestamp", getTimeStamp());
            signaturePayload.put("merchant_id", preferences.getString("merchantId", PayloadConstants.merchantId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return signaturePayload;
    }

    public static JSONObject generateInitiatePayloadV1(SharedPreferences preferences, String clientAuthToken) {
        JSONObject initiatePayload = new JSONObject();
        try {
            initiatePayload.put("action", PayloadConstants.initAction);
            initiatePayload.put("clientId", preferences.getString("clientId", PayloadConstants.clientId));
            initiatePayload.put("clientAuthToken", clientAuthToken);
            initiatePayload.put("merchantId", preferences.getString("merchantId", PayloadConstants.merchantId));
            initiatePayload.put("customerId", preferences.getString("customerId", PayloadConstants.customerId));
            initiatePayload.put("emailAddress", preferences.getString("emailAddress", PayloadConstants.emailAddress));
            initiatePayload.put("mobileNumber", preferences.getString("mobileNumber", PayloadConstants.mobileNumber));
            initiatePayload.put("environment", preferences.getString("environment", PayloadConstants.environment));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initiatePayload;
    }

    public static JSONObject generateInitiatePayloadV2(SharedPreferences preferences, JSONObject signaturePayload, String signature) {
        JSONObject initiatePayload = new JSONObject();
        try {
            initiatePayload.put("action", PayloadConstants.initAction);
            initiatePayload.put("clientId", preferences.getString("clientId", PayloadConstants.clientId));
            initiatePayload.put("merchantKeyId", preferences.getString("merchantKeyId", PayloadConstants.merchantKeyId));
            initiatePayload.put("signaturePayload", signaturePayload.toString());
            initiatePayload.put("signature", signature);
            initiatePayload.put("environment", preferences.getString("environment", PayloadConstants.environment));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initiatePayload;
    }

    public static JSONObject generateOrderDetails(SharedPreferences preferences, String orderId) {
        JSONObject orderDetails = new JSONObject();
        try {
            orderDetails.put("order_id", orderId);
            orderDetails.put("first_name", preferences.getString("firstName", PayloadConstants.firstName));
            orderDetails.put("last_name", preferences.getString("lastName", PayloadConstants.lastName));
            orderDetails.put("mobile_number", preferences.getString("mobileNumber", PayloadConstants.mobileNumber));
            orderDetails.put("email_address", preferences.getString("emailAddress", PayloadConstants.emailAddress));
            orderDetails.put("customer_id", preferences.getString("customerId", PayloadConstants.customerId));
            orderDetails.put("timestamp", getTimeStamp());
            orderDetails.put("merchant_id", preferences.getString("merchantId", PayloadConstants.merchantId));
            orderDetails.put("amount", preferences.getString("amount", PayloadConstants.amount));
            String mandateType = preferences.getString("mandateOption", PayloadConstants.mandateOption);

            if (!mandateType.equalsIgnoreCase("None")) {
                orderDetails.put("options.create_mandate", mandateType);
                orderDetails.put("mandate_max_amount", preferences.getString("mandateMaxAmount", PayloadConstants.mandateMaxAmount));
                orderDetails.put("metadata.PAYTM_V2:SUBSCRIPTION_EXPIRY_DATE", "2020-12-30");
                orderDetails.put("metadata.PAYTM_V2:SUBSCRIPTION_FREQUENCY_UNIT", "MONTH");
                orderDetails.put("metadata.PAYTM_V2:SUBSCRIPTION_FREQUENCY", "2");
                orderDetails.put("metadata.PAYTM_V2:SUBSCRIPTION_GRACE_DAYS", "0");

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c);
                orderDetails.put("metadata.PAYTM_V2:SUBSCRIPTION_START_DATE", formattedDate);
            }
            orderDetails.put("return_url", PayloadConstants.returnUrl);
            String desc =  "";
            orderDetails.put("description", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    public static JSONObject generateProcessPayloadV1(SharedPreferences preferences, String orderId, String clientAuthToken) {
        JSONObject processPayload = new JSONObject();
        try {
            processPayload.put("action", preferences.getString("action", PayloadConstants.processAction));
            processPayload.put("merchantId", preferences.getString("merchantId", PayloadConstants.merchantId));
            processPayload.put("clientId", preferences.getString("clientId", PayloadConstants.clientId));
            processPayload.put("orderId", orderId);
            processPayload.put("amount", preferences.getString("amount", PayloadConstants.amount));
            processPayload.put("customerId", preferences.getString("customerId", PayloadConstants.customerId));
            processPayload.put("customerEmail", preferences.getString("customerId", PayloadConstants.emailAddress));
            processPayload.put("customerMobile", preferences.getString("mobileNumber", PayloadConstants.mobileNumber));

            ArrayList<String> endUrlArr = new ArrayList<>(Arrays.asList(".*sandbox.juspay.in\\/thankyou.*", ".*sandbox.juspay.in\\/end.*", ".*localhost.*", ".*api.juspay.in\\/end.*"));
            JSONArray endUrls = new JSONArray(endUrlArr);

            processPayload.put("endUrls", endUrls);

            processPayload.put("metadata.JUSPAY:gateway_reference_id", "vodafone");
            processPayload.put("metadata.LAZYPAY:gateway_reference_id", "vodafone");

            processPayload.put("clientAuthToken",clientAuthToken);
            processPayload.put("language", preferences.getString("language", PayloadConstants.language));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processPayload;
    }

    public static JSONObject generateProcessPayloadV2(SharedPreferences preferences, String orderId, JSONObject orderDetails, String signature) {
        JSONObject processPayload = new JSONObject();
        try {
            processPayload.put("action", preferences.getString("action", PayloadConstants.processAction));
            processPayload.put("merchantId", preferences.getString("merchantId", PayloadConstants.merchantId));
            processPayload.put("clientId", preferences.getString("clientId", PayloadConstants.clientId));
            processPayload.put("orderId", orderId);
            processPayload.put("amount", preferences.getString("amount", PayloadConstants.amount));
            processPayload.put("customerId", preferences.getString("customerId", PayloadConstants.customerId));
            processPayload.put("customerMobile", preferences.getString("mobileNumber", PayloadConstants.mobileNumber));

            ArrayList<String> endUrlArr = new ArrayList<>(Arrays.asList(".*sandbox.juspay.in\\/thankyou.*", ".*sandbox.juspay.in\\/end.*", ".*localhost.*", ".*api.juspay.in\\/end.*"));
            JSONArray endUrls = new JSONArray(endUrlArr);

            processPayload.put("endUrls", endUrls);

            processPayload.put("merchantKeyId",preferences.getString("merchantKeyId", PayloadConstants.merchantKeyId));
            processPayload.put("orderDetails", orderDetails.toString());
            processPayload.put("signature", signature);
            processPayload.put("language", preferences.getString("language", PayloadConstants.language));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processPayload;
    }

    public static JSONObject getPaymentsPayload(SharedPreferences preferences, String requestId, JSONObject payload) {
        JSONObject paymentsPayload = new JSONObject();
        try {
            paymentsPayload.put("requestId", requestId);
            paymentsPayload.put("service", preferences.getString("service", PayloadConstants.service));
            paymentsPayload.put("payload", payload);
            paymentsPayload.put("betaAssets", preferences.getBoolean("betaAssets", PayloadConstants.betaAssets));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paymentsPayload;
    }

    public static String generateRequestId() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        for (int i = 0; i < uuid.length; i++) {
            if (i % 2 != 0) {
                uuid[i] = uuid[i].toUpperCase();
            }
        }
        return TextUtils.join("-", uuid);
    }

    public static String generateOrderId() {
        return "R" + (long) (Math.random() * 10000000000L);
    }

    static void setDefaultsIfNotPresent(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();

        if (!preferences.contains("clientIdPrefetch")) {
            editor.putString("clientIdPrefetch", PayloadConstants.clientId);
        }
        if (!preferences.contains("betaAssetsPrefetch")) {
            editor.putBoolean("betaAssetsPrefetch", PayloadConstants.betaAssets);
        }
        if (!preferences.contains("firstName")) {
            editor.putString("firstName", PayloadConstants.firstName);
        }
        if (!preferences.contains("lastName")) {
            editor.putString("lastName", PayloadConstants.lastName);
        }
        if (!preferences.contains("mobileNumber")) {
            editor.putString("mobileNumber", PayloadConstants.mobileNumber);
        }
        if (!preferences.contains("emailAddress")) {
            editor.putString("emailAddress", PayloadConstants.emailAddress);
        }
        if (!preferences.contains("customerId")) {
            editor.putString("customerId", PayloadConstants.customerId);
        }
        if (!preferences.contains("amount")) {
            editor.putString("amount", PayloadConstants.amount);
        }
        if (!preferences.contains("language")) {
            editor.putString("language", PayloadConstants.language);
        }
        if (!preferences.contains("mandateOption")) {
            editor.putString("mandateOption", PayloadConstants.mandateOption);
        }
        if (!preferences.contains("mandateMaxAmount")) {
            editor.putString("mandateMaxAmount", PayloadConstants.mandateMaxAmount);
        }
        if (!preferences.contains("merchantId")) {
            editor.putString("merchantId", PayloadConstants.merchantId);
        }
        if (!preferences.contains("clientId")) {
            editor.putString("clientId", PayloadConstants.clientId);
        }
        if (!preferences.contains("service")) {
            editor.putString("service", PayloadConstants.service);
        }
        if (!preferences.contains("merchantKeyId")) {
            editor.putString("merchantKeyId", PayloadConstants.merchantKeyId);
        }
        if (!preferences.contains("signatureURL")) {
            editor.putString("signatureURL", PayloadConstants.signatureURL);
        }
        if (!preferences.contains("apiKey")) {
            editor.putString("apiKey", PayloadConstants.apiKey);
        }
        if (!preferences.contains("environment")) {
            editor.putString("environment", PayloadConstants.environment);
        }
        if (!preferences.contains("action")) {
            editor.putString("action", PayloadConstants.processAction);
        }
        if (!preferences.contains("betaAssets")) {
            editor.putBoolean("betaAssets", PayloadConstants.betaAssets);
        }

        editor.apply();
    }
}
