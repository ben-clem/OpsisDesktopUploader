package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
                String requestBody = "api_key=" + api;
                HttpResponse<String> response;

                try {

                    // Building client and request
                    System.setProperty("javax.net.ssl.trustStore", "test.jks");
                    System.setProperty("javax.net.ssl.trustStoreType", "jks");

                    HttpClient client = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_1_1)
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .followRedirects(Redirect.NORMAL)
                            .connectTimeout(Duration.ofSeconds(30))
                            .authenticator(Authenticator.getDefault())
                            .build();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://" + url + "/service.php?urlaction=recherche"))
                            .timeout(Duration.ofSeconds(30))
                            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                            .build();

                    response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());

                    System.out.println(response.toString() + "\n-------\n"
                            + "authenticator : " + client.authenticator() + "\n-------\n"
                            + "\n-------\n"
                            + "\n-------\n"
                            + response.headers() + "\n-------\n"
                            + response.version() + "\n-------\n"
                            + response.body());

                } catch (IOException | InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }

                // Fin connexion
                // Save info in file (json)
                JSONObject obj = new JSONObject();
                obj.put("url", url);
                obj.put("api-key", api);
                obj.put("name", nom);

                try (FileWriter file = new FileWriter("connection-info.json")) {
                    file.write(obj.toString());
                    System.out.println("Successfully copied JSON Object to File...");
                    System.out.println("\nJSON Object: " + obj);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }

                // Set needRefresh and refreshType
                needRefresh = true;
                refreshType = "loadUploadPanel";

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

        try (Reader reader = new FileReader("connection-info.json")) {

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
