package com.jvlei.modelrouter.dicom;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CGetHandler extends ReceivedObjectHandler {

    public Runnable exe;

    public CGetHandler(Runnable exe) {
        this.exe = exe;
    }

    public CGetHandler() {
    }

    @Override
    public void sendReceivedObjectIndication(String filename, String transferSyntax, String calledAetTitle)
            throws DicomNetworkException, DicomException, IOException {

        log.info("Incoming data from " + calledAetTitle + "...");
        log.info("filename:" + filename);
        log.info("transferSyntax:" + transferSyntax);

        if (exe != null) {
            exe.run();
        }

    }

}
