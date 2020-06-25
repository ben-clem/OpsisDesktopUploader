package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.model.Medias;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.openide.util.Exceptions;

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
            
            // Adding filter
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory()
                            || f.getName().toLowerCase().endsWith(".mp4")
                            || f.getName().toLowerCase().endsWith(".mov")
                            || f.getName().toLowerCase().endsWith(".jpg")
                            || f.getName().toLowerCase().endsWith(".jpeg")
                            || f.getName().toLowerCase().endsWith(".png");
                }

                @Override
                public String getDescription() {
                    return "Images et vidéos";
                }

            });

            // Show the dialog; wait until dialog is closed
            chooser.showDialog(theView, "Choisissez les fichiers à uploader");

            // Retrieve the selected files.
            File[] files = chooser.getSelectedFiles();
            
            // Adding them to the model
            for (File f : files) {
                theModel.addFile(f);
            }
            
            theView.showMedias(theModel.getMedias());
            
            needRefresh = true;
            refreshType = "reloadUploadPanel";

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

    //////////////
    // METHODES //
    //////////////
    /**
     * base constructor
     *
     * @param theView
     */
    public UplPanCon(UploadPanel theView) {

        this.theView = theView;
        this.theModel = new Medias();

        // Connecting action listeners
        this.theView.addOpenButtonListener(new OpenButtonListener());
        this.theView.addDecoButtonListener(new DecoButtonListener());

    }

}
