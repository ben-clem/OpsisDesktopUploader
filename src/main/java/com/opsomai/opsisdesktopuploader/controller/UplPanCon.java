package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.model.Media;
import com.opsomai.opsisdesktopuploader.model.Medias;
import com.opsomai.opsisdesktopuploader.utility.FileDrop;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.openide.util.Exceptions;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Upload Panel Controller
 */
public class UplPanCon extends PanCon {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private final UploadPanel theView;
    private final Medias theModel;

    //////////////////////
    // ACTION LISTENERS //
    //////////////////////
    /**
     * Implementation of the open button listener
     */
    class OpenButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // Creating the JFileChooser in read-only mode
            Boolean old = UIManager.getBoolean("FileChooser.readOnly");
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
            JFileChooser chooser = new JFileChooser();
            UIManager.put("FileChooser.readOnly", old);

            chooser.setMultiSelectionEnabled(true);

//            // Adding filter
//            chooser.setAcceptAllFileFilterUsed(false);
//            chooser.addChoosableFileFilter(new FileFilter() {
//
//                @Override
//                public boolean accept(File f) {
//                    return f.isDirectory()
//                            || f.getName().toLowerCase().endsWith(".mp4")
//                            || f.getName().toLowerCase().endsWith(".mov")
//                            || f.getName().toLowerCase().endsWith(".jpg")
//                            || f.getName().toLowerCase().endsWith(".jpeg")
//                            || f.getName().toLowerCase().endsWith(".png");
//                }
//
//                @Override
//                public String getDescription() {
//                    return "Images et vidéos";
//                }
//
//            });
            // Show the dialog; wait until dialog is closed
            int result = chooser.showDialog(theView, "Choisissez les fichiers à uploader");

            if (result == JFileChooser.APPROVE_OPTION) {

                // Retrieve the selected files.
                File[] files = chooser.getSelectedFiles();

                // Converting to ArrayList
                List<File> filesList = Arrays.asList(files);

                // Sorting the list alphabetically in descending order
                Collections.sort(filesList, new SortIgnoreCase());

                // Adding them to the model
                filesList.forEach(f -> {

                    System.out.println("___adding " + f.getName());
                    theModel.addMedia(f);

                });

                // Sorting all by index
                theModel.sortAllByIndex();

                theView.displayMediasInfo(theModel.getMedias());
                theView.addCancelButtonsListeners(new CancelButtonListener());

                System.out.println("\n___asking for reload from controller");

                needRefresh = true;
                refreshType = "reloadUploadPanel";

                Medias.ThumbnailsWorker thumbsWorker = theModel.new ThumbnailsWorker();

                thumbsWorker.execute();

            } else if (result == JFileChooser.CANCEL_OPTION) {
                // Do nothing
            }

        }
    }

    /**
     * Implementation of the deco button listener
     */
    class DecoButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                // clear JSON file
                new FileWriter("connection-info.json", false).close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            needRefresh = true;
            refreshType = "loadConnectionPanel";

        }
    }

    /**
     * Implementation of the file drop listener
     */
    class FileDropListener implements FileDrop.Listener {

        @Override
        public void filesDropped(java.io.File[] files) {

            // handle file drop
            // Converting to ArrayList
            List<File> filesList = Arrays.asList(files);

            // Sorting the list alphabetically in descending order
            Collections.sort(filesList, new SortIgnoreCase());

            // Adding them to the model
            filesList.forEach(f -> {

                System.out.println("___adding " + f.getName());
                theModel.addMedia(f);

            });

            // Sorting all by index
            theModel.sortAllByIndex();

            theView.displayMediasInfo(theModel.getMedias());
            theView.addCancelButtonsListeners(new CancelButtonListener());

            System.out.println("\n___asking for reload from controller");

            needRefresh = true;
            refreshType = "reloadUploadPanel";

            Medias.ThumbnailsWorker thumbsWorker = theModel.new ThumbnailsWorker();

            thumbsWorker.execute();

        }   // end filesDropped

    }

    /**
     * Implementation of the Upload button listener
     */
    class UploadButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // CONNEXION API
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = null;
            try {
                sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(new TrustSelfSignedStrategy())
                        .build();
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
                Exceptions.printStackTrace(ex);
            }

            // Allow TLSv1.2 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            try (CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build()) {

                HttpPost httpPost = new HttpPost("https://" + url + "/service.php?urlaction=upload");

                MultipartEntityBuilder mulitEntiBuilder = MultipartEntityBuilder.create();

                mulitEntiBuilder.addPart("api_key", new StringBody(api, ContentType.TEXT_PLAIN));

                theModel.getMedias().forEach(media -> {
                    mulitEntiBuilder.addPart("media[]", new FileBody(media.getFile()));
                });

                HttpEntity formEntity = mulitEntiBuilder.build();

                httpPost.setEntity(formEntity);

                System.out.println("Executing request " + httpPost.getRequestLine());

                // Getting the response
                try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);

                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    System.out.println("----------------------------------------");
                    System.out.println(responseBody);
                    System.out.println("----------------------------------------");

//                    // Handling response
//                    if ("<rsp stat=\"ko\"><message>Authentification failed".equals(responseBody.substring(0, 47))) {
//
//                        theView.popupError("Authentification failed");
//
//                    } else if ("<rsp stat='ok'>".equals(responseBody.substring(39, 54))) {
//
//                        // Fin connexion
//                        // Save info in file (json)
//                        JSONObject obj = new JSONObject();
//                        obj.put("url", url);
//                        obj.put("api-key", api);
//                        obj.put("name", nom);
//
//                        try (FileWriter file = new FileWriter("connection-info.json")) {
//                            file.write(obj.toString());
//                            System.out.println("Successfully copied JSON Object to File...");
//                            System.out.println("\nJSON Object: " + obj);
//                        } catch (Exception e) {
//                            e.printStackTrace(System.err);
//                        }
//
//                        // Set needRefresh and refreshType
//                        needRefresh = true;
//                        refreshType = "loadUploadPanel";
//
//                    } else {
//                        theView.popupError("Unknown response:\n"
//                                + "--------------------\n"
//                                + responseBody);
//                    }
                    EntityUtils.consume(entity);
                }

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

    /**
     * Implementation of the Cancel button listener
     */
    class CancelButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            int index = Integer.parseInt(((JButton) e.getSource()).getName());

            System.out.println("___cancel button " + index + " clicked");

            theModel.dumpMedia(index);
            theView.removeFile(index);

            needRefresh = true;
            refreshType = "reloadUploadPanel";

        }
    }

    /**
     * Implementation of the Cancel ALL button listener
     */
    class CancelAllButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            theModel.dumpMedias();
            theView.emptyFilesPanel();

            needRefresh = true;
            refreshType = "reloadUploadPanel";

        }
    }

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     * @param api_key
     * @param url
     */
    public UplPanCon(UploadPanel theView, String api_key, String url) {

        this.theView = theView;
        this.theModel = new Medias(theView, this);

        this.api = api_key;
        this.url = url;

        // Connecting action listeners
        this.theView.addOpenButtonListener(new OpenButtonListener());
        this.theView.addDecoButtonListener(new DecoButtonListener());
        this.theView.addFileDropListener(new FileDropListener());

        this.theView.addUploadButtonListener(new UploadButtonListener());
        this.theView.addCancelAllButtonListener(new CancelAllButtonListener());

    }

    /**
     * Utility class to sort files without looking at the case
     */
    public class SortIgnoreCase implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            String s1 = o1.getName();
            String s2 = o2.getName();
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    @Override
    public void setNeedRefresh(Boolean b) {
        super.setNeedRefresh(b);
    }

    @Override
    public void setRefreshType(String s) {
        super.setRefreshType(s);
    }

}
