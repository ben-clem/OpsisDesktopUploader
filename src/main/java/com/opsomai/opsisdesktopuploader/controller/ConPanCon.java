package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
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
                // connexion (requète recherche vide, try sinon popup)
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

                // JSON reader test --> Good mais pas ici
                JSONParser parser = new JSONParser();

                try (Reader reader = new FileReader("connection-info.json")) {

                    JSONObject jsonObject = (JSONObject) parser.parse(reader);
                    System.out.println(jsonObject);

                    String readUrl = (String) jsonObject.get("url");
                    System.out.println("readUrl = " + readUrl);

                    String readApiKey = (String) jsonObject.get("api-key");
                    System.out.println("readApiKey = " + readApiKey);

                    String readName = (String) jsonObject.get("name");
                    System.out.println("readName = " + readName);

                } catch (org.json.simple.parser.ParseException | IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            // Set needRefresh and refreshType
            needRefresh = true;
            refreshType = "loadUploadPanel";
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

        this.theView = theView;

        // Connecting the action listener to the view
        this.theView.addConnButtonListener(new ConnButtonListener());

    }

}
