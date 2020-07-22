package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.utility.Global;
import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;

/**
 * Connection Panel Controller
 */
public class ConPanCon extends PanCon {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    protected ConnectionPanel theView;

    //////////////////////
    // ACTION LISTENERS //
    //////////////////////
    /**
     * Implementation of the connection button listener
     */
    class ConnButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {

            url = theView.getUrl();
            api = theView.getApi();
            nom = theView.getNom();

            System.out.println("-- Connecting as:");
            System.out.println("url = " + url);
            System.out.println("API = " + api);
            System.out.println("nom = " + nom);

            // Good jusque là
            // Il faut mtn check que les champs sont bien remplis,
            // que le format est bon
            // puis se connecter et si c'est bon :
            // sauvegarder les infos de connexion dans un fichier et passer à la suite.
            if (url.isEmpty() || api.isEmpty()) {
                theView.popupWarning("Veuillez remplir les 2 premiers champs.");
            } else {

                // vérifier format ?
                // CONNEXION (requète recherche vide, try sinon popup)
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = null;
                try {
                    sslcontext = SSLContexts.custom()
                            .loadTrustMaterial(new TrustSelfSignedStrategy())
                            .build();
                } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
                    Exceptions.printStackTrace(ex);
                }

                // Allow TLSv1 protocol only
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslcontext,
                        new String[]{"TLSv1.2"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());

                try (CloseableHttpClient httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build()) {

                    HttpPost httpPost = new HttpPost("https://" + url + "/service.php?urlaction=recherche");

                    List<NameValuePair> nvps = new ArrayList<>();
                    nvps.add(new BasicNameValuePair("api_key", api));
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

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

                        // Handling response
                        if ("<rsp stat=\"ko\"><message>Authentification failed".equals(responseBody.substring(0, 47))) {

                            theView.popupError("Authentification failed");

                        } else if ("<rsp stat='ok'>".equals(responseBody.substring(39, 54))) {

                            // Fin connexion
                            // Save info in file (json)
                            JSONObject obj = new JSONObject();
                            obj.put("url", url);
                            obj.put("api-key", api);
                            obj.put("name", nom);

                            try (FileWriter file = new FileWriter(Global.getWorkingDirPrefix() + "resources/connection-info.json")) {
                                file.write(obj.toString());
                                System.out.println("Successfully copied JSON Object to File...");
                                System.out.println("\nJSON Object: " + obj);
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                            }

                            // Set needRefresh and refreshType
                            needRefresh = true;
                            refreshType = "loadUploadPanel";

                        } else {
                            theView.popupError("Unknown response:\n"
                                    + "--------------------\n"
                                    + responseBody);
                        }

                        EntityUtils.consume(entity);
                    }

                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }

        }

    }

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     */
    public ConPanCon(ConnectionPanel theView) {

        String readUrl;
        String readApiKey;
        String readName;

        // JSON reader
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(Global.getWorkingDirPrefix() + "resources/connection-info.json")) {

            if (reader.ready()) {
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                System.out.println(jsonObject);

                readUrl = (String) jsonObject.get("url");
                System.out.println("readUrl = " + readUrl);

                readApiKey = (String) jsonObject.get("api-key");
                System.out.println("readApiKey = " + readApiKey);

                readName = (String) jsonObject.get("name");
                System.out.println("readName = " + readName);

                if (readUrl.isEmpty() || readApiKey.isEmpty()) {

                    this.theView = theView;

                    // Connecting the action listener to the view
                    this.theView.addConnButtonListener(new ConnButtonListener());

                } else {

                    url = readUrl;
                    api = readApiKey;
                    nom = readName;

                    // Set needRefresh and refreshType
                    needRefresh = true;
                    refreshType = "loadUploadPanel";
                }
            } else {
                System.out.println("-- connection-info.json is empty");

                this.theView = theView;
                // Connecting the action listener to the view
                this.theView.addConnButtonListener(new ConnButtonListener());
            }

        } catch (org.json.simple.parser.ParseException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

}
