package com.opsomai.opsisdesktopuploader.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.entity.mime.content.FileBody;

public class MyFileBody extends FileBody {

    private IStreamListener listener;

    public MyFileBody(File file) {
        super(file);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        CountingOutputStream output = new CountingOutputStream(out) {
            @Override
            protected void beforeWrite(int n) {
                if (listener != null && n != 0) {
                    listener.counterChanged(n);
                }
                super.beforeWrite(n);
            }
        };
        super.writeTo(output);

    }

    public void setListener(IStreamListener listener) {
        this.listener = listener;
    }

    public IStreamListener getListener() {
        return listener;
    }

}
