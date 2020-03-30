package in.juspay.testIntegrationApp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SignatureAPI extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder result = new StringBuilder();
        try {
            String orderUrl = strings[0] + "?payload=" + strings[1];
            Log.wtf("SignedByHarsh", orderUrl);

            HttpsURLConnection connection = (HttpsURLConnection) (new URL(orderUrl).openConnection());
            connection.setRequestMethod("GET");

            int respCode = connection.getResponseCode();
            InputStreamReader respReader;

            if ((respCode < 200 || respCode >= 300) && respCode != 302) {
                respReader = new InputStreamReader(connection.getErrorStream());
            } else {
                respReader = new InputStreamReader(connection.getInputStream());
            }

            BufferedReader in = new BufferedReader(respReader);
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }

            return result.toString();
        } catch (Exception ignored) {
            return result.toString();
        }
    }
}
