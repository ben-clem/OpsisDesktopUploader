package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import com.opsomai.opsisdesktopuploader.view.Window;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.ZonedDateTime;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;

/**
 * Controller de la fenêtre
 */
public class OpsisDesktopUploader {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private Window win;

    private Boolean needRefresh;
    private String refreshType;

    private UploadPanel uploadPanel;

    //////////////
    // METHODES //
    //////////////
    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {

        // Making the look and feel universal
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(System.err);
        }

        // Sets the buttons press key to enter
        UIManager.put("Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]{
            "ENTER", "pressed",
            "released ENTER", "released"
        }));
        
        // Configure log4j for MIME type checking with JMimeType (ditched because too slow)
        // BasicConfigurator.configure();

        // Instanciating the controller
        OpsisDesktopUploader winCon = new OpsisDesktopUploader();

        // Creating the window
        winCon.win = new Window();

        // Creating the first view
        ConnectionPanel conPan = new ConnectionPanel();

        // JSON reader
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("connection-info.json")) {

            if (reader.ready()) {
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                System.out.println(jsonObject);

                String readUrl = (String) jsonObject.get("url");
                System.out.println("readUrl = " + readUrl);

                String readApiKey = (String) jsonObject.get("api-key");
                System.out.println("readApiKey = " + readApiKey);

                if (readUrl.isEmpty() || readApiKey.isEmpty()) {

                    // Showing
                    winCon.win.setContentPane(conPan);
                    winCon.win.setVisible(true);

                }
            } else {
                // Showing
                winCon.win.setContentPane(conPan);
                winCon.win.setVisible(true);
            }

        } catch (org.json.simple.parser.ParseException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        // Creating its controller
        ConPanCon conPanCon = new ConPanCon(conPan);

        // Starting update loop
        winCon.updateLoop(winCon, conPanCon);

    }

    /**
     * update loop
     */
    private synchronized void updateLoop(OpsisDesktopUploader winCon, PanCon panCon) {

        while (true) { // while app is running

            // delay
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }

            // get refresh info
            needRefresh = panCon.getNeedRefresh();
            refreshType = panCon.getRefreshType();

            if (needRefresh == true) {

                System.out.println("-- needRefresh = " + needRefresh);
                System.out.println("-- updating at " + ZonedDateTime.now());
                System.out.println("-- refreshType = " + refreshType);

                switch (refreshType) {

                    case "loadUploadPanel":

                        uploadPanel = new UploadPanel(panCon.getName(), panCon.getUrl());

                        UplPanCon uplPanCon = new UplPanCon(uploadPanel);

                        winCon.win.setContentPane(uploadPanel);
                        winCon.win.setVisible(true);

                        panCon = uplPanCon;

                        break;

                    case "reloadUploadPanel":

                        winCon.win.setContentPane(uploadPanel);
                        winCon.win.setVisible(true);

                        panCon.setNeedRefresh(false);

                        break;

                    case "loadConnectionPanel":

                        ConnectionPanel conPan = new ConnectionPanel();
                        ConPanCon conPanCon = new ConPanCon(conPan);
                        winCon.win.setContentPane(conPan);
                        winCon.win.setVisible(true);

                        panCon = conPanCon;

                        break;

//                    case "":
//
//                      
//                        break;
                }

                // updating controller
//                currentUser = panCon.getCurrentUser();
            }
        }

    }
}
