package com.opsomai.opsisdesktopuploader.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;

/**
 * Ecran de connexion
 */
public final class ConnectionPanel extends JPanel {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private Color bg = new Color(255, 255, 255);
    private Color fg = new Color(220, 0, 0);

    private JLabel logoLabel;
    private JLabel titleLabel;

    private JPanel connectionForm;
    private JPanel col1;
    private JPanel col2;
    private JTextField url;
    private JTextField api;
    private JTextField nom;

    private JButton connButton;

    //////////////
    // METHODES //
    //////////////
    /**
     * default constructor
     */
    public ConnectionPanel() {

        // background and layout
        setBackground(bg);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.add(Box.createRigidArea(new Dimension(0, 10)));

        // Logo
        try {
            Image img = ImageIO.read(new FileInputStream("img/logo.jpg"));
            img = img.getScaledInstance(-1, 100, Image.SCALE_SMOOTH);
            ImageIcon logo = new ImageIcon(img);
            logoLabel = new JLabel();
            logoLabel.setIcon(logo);

            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(logoLabel);

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        this.add(Box.createRigidArea(new Dimension(0, 12)));

        // Title
        titleLabel = new JLabel("<html>Opsis Desktop Uploader<br>"
                + "<br/>"
                + "<br/>"
                + "</html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(titleLabel);

        this.add(Box.createVerticalGlue());

        // Connection Form (line_axis containing 2 cols)
        connectionForm = new JPanel();
        connectionForm.setLayout(new BoxLayout(connectionForm, BoxLayout.LINE_AXIS));

        // Col 1
        col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.PAGE_AXIS));

        JLabel urlLabel = new JLabel("URL de la médiathèque* :");
        urlLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        urlLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        col1.add(urlLabel);

        JLabel apiLabel = new JLabel("Clé API utilisateur* :");
        apiLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        apiLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        col1.add(apiLabel);

        JLabel nomLabel = new JLabel("(Nom de la médiathèque) :");
        nomLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        nomLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        col1.add(nomLabel);

        col1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        col1.setAlignmentY(CENTER_ALIGNMENT);
        connectionForm.add(col1);

        // Col 2
        col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.PAGE_AXIS));

        url = new JTextField();
        url.setMaximumSize(new Dimension(300, 20));
        url.setAlignmentX(Component.LEFT_ALIGNMENT);
        col2.add(url);

        col2.add(Box.createRigidArea(new Dimension(0, 10)));

        api = new JTextField();
        api.setMaximumSize(new Dimension(300, 20));
        api.setAlignmentX(Component.LEFT_ALIGNMENT);
        col2.add(api);

        col2.add(Box.createRigidArea(new Dimension(0, 10)));

        nom = new JTextField();
        nom.setMaximumSize(new Dimension(300, 20));
        nom.setAlignmentX(Component.LEFT_ALIGNMENT);
        col2.add(nom);

        col2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        col2.setAlignmentY(CENTER_ALIGNMENT);
        connectionForm.add(col2);

        connectionForm.setMinimumSize(new Dimension(500, 500));
        this.add(connectionForm);
        // Fin Connection Form

        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // Conn Button
        connButton = new JButton("Se connecter à la médiathèque");
        connButton.setBackground(Color.WHITE);
        connButton.setForeground(Color.BLACK);
        connButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(connButton);

        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalGlue());

    }

    /**
     * adds an ActionListener to the ConnButton
     *
     * @param listenForConnButton ActionListener added by the controller
     */
    public void addConnButtonListener(ActionListener listenForConnButton) {

        connButton.addActionListener(listenForConnButton);

    }

    /**
     * simple popup
     *
     * @param message
     */
    public void popup(String message) {

        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.PLAIN_MESSAGE);

    }

    /**
     * Warning popup
     *
     * @param message
     */
    public void popupWarning(String message) {

        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);

    }

    /**
     * Error popup
     *
     * @param message
     */
    public void popupError(String message) {

        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);

    }
    
    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    /**
     * @return the url
     */
    public String getUrl() {

        return url.getText();

    }

    /**
     * @return the api key
     */
    public String getApi() {

        return api.getText();

    }

    /**
     * @return the name
     */
    public String getNom() {

        return nom.getText();

    }

}







