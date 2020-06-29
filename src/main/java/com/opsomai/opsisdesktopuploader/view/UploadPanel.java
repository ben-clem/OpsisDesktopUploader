package com.opsomai.opsisdesktopuploader.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import javax.activation.MimetypesFileTypeMap;

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
    private JPanel filesPanel;

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
                    createImageIcon("img/Open16.gif"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        choicePanel.add(openButton);

        JLabel lab2 = new JLabel(" ou les glisser-déposer en dessous :");
        choicePanel.add(lab2);

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

        filesPanel.setBackground(fg);
        //filesPanel.setMinimumSize(new Dimension(500, 100));

        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.gridx = 0;
        c3.gridy = 2;
        c3.gridwidth = 3;
        c3.weighty = 1;
        c3.insets = new Insets(5, 5, 5, 5);
        this.add(filesPanel, c3);

        // buttons
        Component box = Box.createHorizontalStrut(1);
        GridBagConstraints c6 = new GridBagConstraints();
        c6.fill = GridBagConstraints.BOTH;
        c6.gridx = 0;
        c6.gridy = 3;
        c6.weightx = 0.3;
        this.add(box, c6);

        JButton start = new JButton("Démarrer l'upload");
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

        JButton cancel = new JButton("X Annuler");
        GridBagConstraints c5 = new GridBagConstraints();
        c5.fill = GridBagConstraints.BOTH;
        c5.gridx = 2;
        c5.gridy = 3;
        c5.weightx = 0.2;
        c5.insets = new Insets(5, 5, 10, 5);
        this.add(cancel, c5);
    }

    public void showMedias(ArrayList<File> medias) {

        ImageIO.setUseCache(false);
        
        medias.stream().forEach((media) -> {

            System.out.println("-beginning working on: " + media.getName());
            LocalDateTime beginDate = LocalDateTime.now();

            Image newImg = null;

            // Media Display
            // Getting the thumbnail
            // If it's an image: scale it
//
//  Solution using MIME type checking in the file header with JMimeMagic
//  more secure because it prevents file with a wrong extension to get through
//  but too slow to be used (approx. 4sec by file)
//
//            try {
//                String mimeType = Magic.getMagicMatch(media, false).getMimeType();
//
//                if (mimeType.startsWith("image/")) {
//
//
            String mimetype = new MimetypesFileTypeMap().getContentType(media);
            String type = mimetype.split("/")[0];
            
            if (type.equals("image")) {
                System.out.println("It's an image");
                // It's an image.

                BufferedImage img;
                try {
System.out.println("1");
                    img = ImageIO.read(media);
System.out.println("1.1");

                    int width = img.getWidth();
                    int height = img.getHeight();
System.out.println("2");

                    Dimension initDim = new Dimension(width, height);
                    Dimension boundaries = new Dimension(75, 75);
                    Dimension newDim = getScaledDimension(initDim, boundaries);
System.out.println("3");

                    System.out.println("-scaling img: " + media.getName());
                    newImg = img.getScaledInstance(newDim.width, newDim.height, Image.SCALE_SMOOTH);

                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            } else {

                // It's not an image.
                // If it's a video:
                // Else: put a generic file icon (could get the corresponding file system icon)
            }

            // Display
            System.out.println(
                    "- adding img: " + media.getName());
            ImageIcon newImgIcon = new ImageIcon(newImg);
            JLabel m = new JLabel(newImgIcon);

            filesPanel.add(m);

            LocalDateTime endDate = LocalDateTime.now();
            Duration duration = Duration.between(beginDate, endDate);
            long diff = Math.abs(duration.toSeconds());

            System.out.println(
                    "----- took " + diff + "s");
        }
        );

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
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Get scaled dimensions within boundaries while conserving aspect ratio
     *
     * @param imgSize
     * @param boundary
     * @return
     */
    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

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

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    /**
     * @return the open button
     */
    public JButton getOpenButton() {
        return openButton;
    }
}
