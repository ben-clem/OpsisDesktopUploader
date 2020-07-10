package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.utility.OSValidator;
import com.opsomai.opsisdesktopuploader.view.ConnectionPanel;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import com.opsomai.opsisdesktopuploader.view.Window;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.ZonedDateTime;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultEditorKit;
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

        // Getting the OSType
        String OSType = OSValidator.getOSType();

        // Fixing copy / paste on macOS
        if ("macOS".equals(OSType)) {
            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
        }

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

                System.out.println("\n-- needRefresh = " + needRefresh);
                System.out.println("-- updating at " + ZonedDateTime.now());
                System.out.println("-- refreshType = " + refreshType);

                switch (refreshType) {

                    case "loadUploadPanel":
                        
                        String name = panCon.getName();
                        String api_key = panCon.getApiKey();
                        String url = panCon.getUrl();
                        
                        uploadPanel = new UploadPanel(panCon.getName(), panCon.getUrl());

                        UplPanCon uplPanCon = new UplPanCon(uploadPanel, name, api_key, url);

                        winCon.win.setContentPane(uploadPanel);
                        winCon.win.setVisible(true);

                        panCon = uplPanCon;

                        break;

                    case "reloadUploadPanel":

                        System.out.println("\n=== reload order is received in main thread ===");

                        int scrollPos = uploadPanel.getScrollBarVerticalPosition();

                        winCon.win.setContentPane(uploadPanel);
                        winCon.win.setVisible(true);

                        uploadPanel.setScrollBarVerticalPosition(scrollPos);

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
