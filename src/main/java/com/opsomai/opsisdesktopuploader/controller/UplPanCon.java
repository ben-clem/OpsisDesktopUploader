package com.opsomai.opsisdesktopuploader.controller;

import com.opsomai.opsisdesktopuploader.model.Medias;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
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
        this.theModel = new Medias(theView, this);

        // Connecting action listeners
        this.theView.addOpenButtonListener(new OpenButtonListener());
        this.theView.addDecoButtonListener(new DecoButtonListener());

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
