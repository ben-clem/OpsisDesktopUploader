package com.opsomai.opsisdesktopuploader.view;

import com.opsomai.opsisdesktopuploader.model.Media;
import com.opsomai.opsisdesktopuploader.model.ProgressPair;
import com.opsomai.opsisdesktopuploader.utility.MimeTypesFixer;
import com.opsomai.opsisdesktopuploader.model.Thumbnail;
import com.opsomai.opsisdesktopuploader.utility.FileDrop;
import com.opsomai.opsisdesktopuploader.utility.Global;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;

/**
 * Upload Screen
 */
public final class UploadPanel extends JPanel {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private String name;
    private String url;

    private Color bg = new Color(255, 255, 255);
    private Color fg = new Color(220, 0, 0);

    private JButton deco;
    private JButton openButton;
    private JButton start = new JButton("Démarrer l'upload");
    private JButton cancel;

    private JPanel filesPanel;
    private JScrollPane scrollPane;

    private Map<Integer, JLabel> thumbnailsMap = new HashMap<>();
    private Map<Integer, JTextField> titlesMap = new HashMap<>();
    private Map<Integer, JLabel> typesMap = new HashMap<>();
    private Map<Integer, JProgressBar> progressMap = new HashMap<>();
    private Map<Integer, JButton> cancelMap = new HashMap<>();

    private boolean everyProgressIs100 = false;
    private JDialog waitingPopup;

    //////////////
    // METHODES //
    //////////////
    public UploadPanel(String name, String url) {

        // background and layout
        setBackground(bg);
        setLayout(new GridBagLayout());

        Component box2 = Box.createHorizontalStrut(1);
        GridBagConstraints c7 = new GridBagConstraints();
        c7.fill = GridBagConstraints.BOTH;
        c7.gridx = 0;
        c7.gridy = 0;
        c7.weightx = 0.3;
        this.add(box2, c7);

        JLabel topLabel = new JLabel("Connecté à la médiathèque : " + name + " (" + url + ")");
        topLabel.setForeground(fg);

        GridBagConstraints c1 = new GridBagConstraints();
        //c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.gridwidth = 1;
        c1.weightx = 0.5;
        c1.weighty = 0.02;
        c1.insets = new Insets(5, 5, 0, 5);
        c1.anchor = GridBagConstraints.CENTER;
        this.add(topLabel, c1);

        deco = new JButton("Déconnexion");
        GridBagConstraints c8 = new GridBagConstraints();
        c8.gridx = 2;
        c8.gridy = 0;
        c8.weightx = 0;
        c8.insets = new Insets(5, 5, 5, 5);
        c8.anchor = GridBagConstraints.LINE_END;
        this.add(deco, c8);

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.LINE_AXIS));
        choicePanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.BLACK));

        choicePanel.add(Box.createRigidArea(new Dimension(5, 0)));

        JLabel lab1 = new JLabel("Choisir des fichiers : ");
        choicePanel.add(lab1);

        try {
            // File Chooser
            openButton = new JButton("Select files...",
                    createImageIcon(Global.getWorkingDirPrefix() + "resources/img/Open16.gif"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        choicePanel.add(openButton);

        JLabel lab2_1 = new JLabel("  ou les ");
        choicePanel.add(lab2_1);

        JLabel lab2_2 = new JLabel("glisser-déposer");
        Font font = lab2_2.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        attributes.put(TextAttribute.SIZE, 14.5);
        lab2_2.setFont(font.deriveFont(attributes));
        lab2_2.setForeground(new Color(0, 102, 204));
        choicePanel.add(lab2_2);

        JLabel lab2_3 = new JLabel(" en dessous :");
        choicePanel.add(lab2_3);

        // files uploader
        // adding choice Panel
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 3;
        c2.weighty = 0.08;
        c2.insets = new Insets(0, 5, 0, 5);
        c2.anchor = GridBagConstraints.PAGE_START;
        this.add(choicePanel, c2);

        // upload panel
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.PAGE_AXIS));
        filesPanel.setBackground(bg);

        JLabel dropHere = new JLabel("Déposez vos fichiers ici");
        dropHere.setAlignmentX(Component.CENTER_ALIGNMENT);

        Font dropFont = dropHere.getFont();
        Map dropFontAttributes = dropFont.getAttributes();
        dropFontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        dropFontAttributes.put(TextAttribute.SIZE, 24);
        dropHere.setFont(dropFont.deriveFont(dropFontAttributes));
        dropHere.setForeground(fg);

        filesPanel.add(Box.createVerticalGlue());
        filesPanel.add(dropHere);
        filesPanel.add(Box.createVerticalGlue());

        scrollPane = new JScrollPane(filesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.gridx = 0;
        c3.gridy = 2;
        c3.gridwidth = 3;
        c3.weighty = 1;
        c3.insets = new Insets(5, 5, 5, 5);
        this.add(scrollPane, c3);

        // buttons
        Component box = Box.createHorizontalStrut(1);
        GridBagConstraints c6 = new GridBagConstraints();
        c6.fill = GridBagConstraints.BOTH;
        c6.gridx = 0;
        c6.gridy = 3;
        c6.weightx = 0.3;
        this.add(box, c6);

        
        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.BOTH;
        c4.gridx = 1;
        c4.gridy = 3;
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weightx = 0.5;
        c4.weighty = 0.05;
        c4.insets = new Insets(5, 5, 10, 5);
        this.add(start, c4);
        System.out.println("!!! but placed from view init method");

        cancel = new JButton();

        try {
            cancel = new JButton("Annuler", createImageIcon(Global.getWorkingDirPrefix() + "resources/img/icons8-delete-64.png"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        GridBagConstraints c5 = new GridBagConstraints();
        c5.fill = GridBagConstraints.BOTH;
        c5.gridx = 2;
        c5.gridy = 3;
        c5.weightx = 0.2;
        c5.insets = new Insets(5, 5, 10, 5);
        this.add(cancel, c5);
    }

    public void displayMediasInfo(ArrayList<Media> medias) {

        filesPanel.removeAll();

        medias.stream().forEachOrdered(media -> {

            Integer index = media.getIndex();
            File file = media.getFile();

            if (index == 0) {
                filesPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            }

            JPanel eachFilePanel = new JPanel();
            eachFilePanel.setLayout(new BoxLayout(eachFilePanel, BoxLayout.LINE_AXIS));

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Thumbnail loading icon (while separate thread is loading actual thumbnail)
            ImageIcon loading = new ImageIcon(Global.getWorkingDirPrefix() + "resources/img/ajax-loader.gif");
            final JLabel loadingLabel = new JLabel(loading, JLabel.CENTER);
            loadingLabel.setMinimumSize(new Dimension(100, 100));
            loadingLabel.setPreferredSize(new Dimension(100, 100));
            loadingLabel.setMaximumSize(new Dimension(100, 100));

            thumbnailsMap.put(index, loadingLabel);

            eachFilePanel.add(loadingLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Title
            JLabel titleLabel = new JLabel("Titre : ");
            eachFilePanel.add(titleLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(2, 2)));

            JTextField titleField = new JTextField(file.getName());

            titleField.setMinimumSize(new Dimension(200, 20));
            titleField.setPreferredSize(new Dimension(400, 20));
            titleField.setMaximumSize(new Dimension(600, 20));

            eachFilePanel.add(titleField);

            titlesMap.put(index, titleField);

            eachFilePanel.add(Box.createHorizontalGlue());

            // File type
            // what type of file is it?
            String mimetype = new MimetypesFileTypeMap().getContentType(media.getFile());
            System.out.println("__mimetype = " + mimetype);

            // Fixing missing MIME type
            if ("application/octet-stream".equals(mimetype)) {

                String extension = FilenameUtils.getExtension(media.getFile().getName());

                MimeTypesFixer fixer = new MimeTypesFixer();

                try {
                    mimetype = fixer.getMap().get(extension).toString();
                    System.out.println("__fixed missing mimetype = " + mimetype);
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.out.println("__unable to fix MIME type, keeping: " + mimetype);
                }

            }

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            JLabel typeLabel = new JLabel(mimetype);

            typeLabel.setMinimumSize(new Dimension(200, 20));
            typeLabel.setPreferredSize(new Dimension(200, 20));
            typeLabel.setMaximumSize(new Dimension(200, 20));

            typesMap.put(index, typeLabel);
            eachFilePanel.add(typeLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            eachFilePanel.add(Box.createHorizontalGlue());
            eachFilePanel.add(Box.createHorizontalGlue());
            eachFilePanel.add(Box.createHorizontalGlue());

            // Progress Bar
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressBar.setValue(0);
            progressBar.setString("En attente de validation");
            progressBar.setStringPainted(true);

            progressBar.setMinimumSize(new Dimension(200, 20));
            progressBar.setPreferredSize(new Dimension(300, 20));
            progressBar.setMaximumSize(new Dimension(400, 20));

            eachFilePanel.add(progressBar);

            progressMap.put(index, progressBar);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Cancel Button
            JButton cancelButton = new JButton();
            try {
                cancelButton = new JButton(createImageIcon(Global.getWorkingDirPrefix() + "resources/img/icons8-delete-64.png"));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            cancelButton.setMinimumSize(new Dimension(20, 20));
            cancelButton.setPreferredSize(new Dimension(20, 20));
            cancelButton.setMaximumSize(new Dimension(20, 20));

            eachFilePanel.add(cancelButton);
            cancelMap.put(index, cancelButton);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Adding eachFilePanel to filesPanel
            filesPanel.add(eachFilePanel);

            filesPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        }
        );

    }

    public void addThumbnail(Thumbnail thumbnail) {

        thumbnailsMap.replace(thumbnail.getIndex(), new JLabel(thumbnail.getIcon()));

        refreshMediasInfo();

    }

    public void refreshMediasInfo() {

        filesPanel.removeAll();

        Integer number = 0;

        if (thumbnailsMap.size() == titlesMap.size() && titlesMap.size() == progressMap.size() && progressMap.size() == cancelMap.size()) {
            System.out.println("__good matching of components numbers");

            number = thumbnailsMap.size();
        }

        for (int i = 0; i < number; i++) {

            if (i == 0) {
                filesPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            }

            JPanel eachFilePanel = new JPanel();
            eachFilePanel.setLayout(new BoxLayout(eachFilePanel, BoxLayout.LINE_AXIS));

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Thumbnail
            final JLabel loadingLabel = thumbnailsMap.get(i);
            loadingLabel.setMinimumSize(new Dimension(100, 100));
            loadingLabel.setPreferredSize(new Dimension(100, 100));
            loadingLabel.setMaximumSize(new Dimension(100, 100));

            eachFilePanel.add(loadingLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Title
            JLabel titleLabel = new JLabel("Titre : ");
            eachFilePanel.add(titleLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(2, 2)));

            JTextField titleField = titlesMap.get(i);

            titleField.setMinimumSize(new Dimension(200, 20));
            titleField.setPreferredSize(new Dimension(400, 20));
            titleField.setMaximumSize(new Dimension(600, 20));

            eachFilePanel.add(titleField);

            eachFilePanel.add(Box.createHorizontalGlue());

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            JLabel typeLabel = typesMap.get(i);
            eachFilePanel.add(typeLabel);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            eachFilePanel.add(Box.createHorizontalGlue());
            eachFilePanel.add(Box.createHorizontalGlue());
            eachFilePanel.add(Box.createHorizontalGlue());

            // Progress Bar
            JProgressBar progressBar = progressMap.get(i);

            progressBar.setStringPainted(true);

            progressBar.setMinimumSize(new Dimension(200, 20));
            progressBar.setPreferredSize(new Dimension(300, 20));
            progressBar.setMaximumSize(new Dimension(400, 20));

            eachFilePanel.add(progressBar);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Cancel Button
            JButton cancelButton = cancelMap.get(i);

            cancelButton.setMinimumSize(new Dimension(20, 20));
            cancelButton.setPreferredSize(new Dimension(20, 20));
            cancelButton.setMaximumSize(new Dimension(20, 20));

            eachFilePanel.add(cancelButton);

            eachFilePanel.add(Box.createRigidArea(new Dimension(10, 10)));

            // Adding eachFilePanel to filesPanel
            filesPanel.add(eachFilePanel);

            filesPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        }

    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path
     * @return
     * @throws java.io.FileNotFoundException
     */
    protected static ImageIcon createImageIcon(String path) throws FileNotFoundException, IOException {
        Image img = ImageIO.read(new FileInputStream(path));
        img = img.getScaledInstance(-1, 16, Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(img);
        if (img != null) {
            return logo;
        } else {
            System.err.println("__Couldn't find file: " + path);
            return null;
        }
    }

    public void emptyFilesPanel() {
        filesPanel.removeAll();
    }

    public void removeFile(int index) {

        int matchingIndex = index * 2 + 1;

        filesPanel.remove(matchingIndex);
        filesPanel.remove(matchingIndex - 1);

        cancelMap.remove(index);

        System.out.println("__removed file n°" + index);

        // Fixing cancelMap indexes
        Map<Integer, JButton> newMap = new HashMap<>();

        cancelMap.entrySet().forEach((entry) -> {

            System.out.println("__before: " + entry.getKey() + "/" + entry.getValue().getName());

            if (entry.getKey() > index) {

                int newKey = entry.getKey() - 1;
                JButton newBut = entry.getValue();
                newBut.setName(String.valueOf(newKey));

                newMap.put(newKey, newBut);
                System.out.println("__after: " + newKey + "/" + newBut.getName());

            } else {
                newMap.put(entry.getKey(), entry.getValue());
                System.out.println("__after: " + entry.getKey() + "/" + entry.getValue().getName());
            }

        });

        cancelMap = newMap;

    }

    public void disableButtons() {

        deco.setEnabled(false);
        openButton.setEnabled(false);
        start.setEnabled(false);
        cancel.setEnabled(false);

        cancelMap.forEach((index, button) -> {
            button.setEnabled(false);
        });
        
        titlesMap.forEach((index, title) -> {
            title.setEnabled(false);
        });

    }

    public boolean isEveryProgress100() {

        this.everyProgressIs100 = true;

        progressMap.forEach((index, progressBar) -> {

            if (progressBar.getValue() != 100) {
                this.everyProgressIs100 = false;
            }

        });

        return this.everyProgressIs100;

    }

    /**
     * waiting popup
     */
    public void waitingPopup() {

        ImageIcon loading = new ImageIcon(Global.getWorkingDirPrefix() + "resources/img/ajax-loader.gif");
        
        final JOptionPane optionPane = new JOptionPane("Upload en cours de finalisation...\n"
                + "Veuillez patienter.",
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, loading);

        this.waitingPopup = new JDialog();
        this.waitingPopup.setTitle("Message");
        this.waitingPopup.setModal(true);

        this.waitingPopup.setContentPane(optionPane);

        this.waitingPopup.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.waitingPopup.pack();

        this.waitingPopup.setLocationRelativeTo(this);
        this.waitingPopup.setVisible(true);

    }
    
    public void closeWaitingPopup() {
        this.waitingPopup.setVisible(false);
    }
    
    /**
     * simple popup
     *
     * @param message
     */
    public void popup(String message) {

        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.PLAIN_MESSAGE);

    }
    
    public void switchUploadButton(ActionListener listenForNewUploadButton) {
        
        System.out.println("!!! but switch from view");
        
        this.remove(this.start);
        
        this.start = new JButton("Préparer un nouvel upload");
        start.addActionListener(listenForNewUploadButton);
        
        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.BOTH;
        c4.gridx = 1;
        c4.gridy = 3;
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weightx = 0.5;
        c4.weighty = 0.05;
        c4.insets = new Insets(5, 5, 10, 5);
        this.add(start, c4);
        
        System.out.println("!!! but placed from view switch method");
        
        // Enabling deco button
        this.deco.setEnabled(true);
        
    }

//////////////////////
// ACTION LISTENERS //
//////////////////////
    /**
     * adds an ActionListener to the open button
     *
     * @param listenForOpenButton ActionListener added by the controller
     */
    public void addOpenButtonListener(ActionListener listenForOpenButton) {

        openButton.addActionListener(listenForOpenButton);

    }

    /**
     * adds an ActionListener to the deco button
     *
     * @param listenForDecoButton ActionListener added by the controller
     */
    public void addDecoButtonListener(ActionListener listenForDecoButton) {

        deco.addActionListener(listenForDecoButton);

    }

    /**
     * adds an ActionListener for file dropping
     *
     * @param listenForFileDrop
     */
    public void addFileDropListener(FileDrop.Listener listenForFileDrop) {

        FileDrop fileDrop = new FileDrop(this, listenForFileDrop);

    }
   
    /**
     * adds an ActionListener to the upload button
     *
     * @param listenForUploadButton ActionListener added by the controller
     */
    public void addUploadButtonListener(ActionListener listenForUploadButton) {

        start.addActionListener(listenForUploadButton);

    }

    /**
     * adds an ActionListener to each cancel button
     *
     * @param listenForCancelButtons ActionListener added by the controller
     */
    public void addCancelButtonsListeners(ActionListener listenForCancelButtons) {

        cancelMap.forEach((index, button) -> {

            button.addActionListener(listenForCancelButtons);
            button.setName(String.valueOf(index));

        });

    }

    /**
     * adds an ActionListener to the cancel ALL button
     *
     * @param listenForCancelAllButton ActionListener added by the controller
     */
    public void addCancelAllButtonListener(ActionListener listenForCancelAllButton) {

        cancel.addActionListener(listenForCancelAllButton);

    }

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    /**
     * @return the open button
     */
    public JButton getOpenButton() {
        return openButton;
    }

    public JPanel getFilesPanel() {
        return filesPanel;
    }

    public int getScrollBarVerticalPosition() {
        return scrollPane.getVerticalScrollBar().getValue();
    }

    public void setScrollBarVerticalPosition(int position) {
        scrollPane.getVerticalScrollBar().setValue(position);
    }

    public void setEveryProgressToWaiting() {

        this.progressMap.forEach((index, progressBar) -> {
            progressBar.setString("En attente");
        });

        refreshMediasInfo();

    }

    public void setProgress(ProgressPair progress) {

        this.progressMap.get(progress.getIndex()).setValue(progress.getProgress());

        switch (progress.getProgress()) {
            case 0:
                this.progressMap.get(progress.getIndex()).setString("En attente de validation");
                break;
            case 100:
                this.progressMap.get(progress.getIndex()).setString("Envoyé au serveur pour traitement");
                break;
            default:
                this.progressMap.get(progress.getIndex()).setString(String.valueOf(progress.getProgress()) + "%");
                break;
        }

    }
}
