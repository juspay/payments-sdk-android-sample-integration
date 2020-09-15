package in.juspay.mpindemo.service;

import androidx.annotation.Nullable;

import in.juspay.ipc.data.JuspayIPCConstants;
import in.juspay.ipc.services.JuspayCallback;
import in.juspay.ipc.services.JuspayIPCService;


public class JuspayMpinService extends JuspayIPCService {


    @Override
    public void handleRequest(int i, @Nullable String s, JuspayCallback juspayCallback) {
            juspayCallback.onResponse("2123123");
    }
}
