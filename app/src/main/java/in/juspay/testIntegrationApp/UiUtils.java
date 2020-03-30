package in.juspay.testIntegrationApp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UiUtils {

    private static final String BASE_URL = "https://hyperwidget-ppconfig.herokuapp.com/faq/";

    public static void showMessageInModal(Context cont, String header, String message) {
        new MaterialAlertDialogBuilder(cont)
                .setTitle(header)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Copy", (dialogInterface, i) -> {
                    Toast.makeText(cont, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                    copyToClipBoard(cont, header, message);
                })
                .show();
    }

    public static CharSequence getColoredText(String text, String color) {
        return Html.fromHtml("<font color='" + color + "'>" + text + "</font>");
    }

    public static CharSequence getWhiteText(String text) {
        return getColoredText(text, "#ffffff");
    }

    public static void openWebView(Context context, String path) {
        Intent i = new Intent(context, WebViewActivity.class);
        i.putExtra("path", path);
        context.startActivity(i);
    }

    public static void launchInCustomTab(Context context, String path) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        Uri url = Uri.parse(BASE_URL + path);
        customTabsIntent.launchUrl(context, url);
    }

    public static void copyToClipBoard(Context context, String header, String message) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(header, message);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }
}
