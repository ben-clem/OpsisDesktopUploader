package com.opsomai.opsisdesktopuploader.model;

import com.opsomai.opsisdesktopuploader.utility.MimeTypesFixer;
import com.opsomai.opsisdesktopuploader.controller.UplPanCon;
import com.opsomai.opsisdesktopuploader.utility.Global;
import com.opsomai.opsisdesktopuploader.view.UploadPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.SSLContext;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

/**
 * Classe médias package model
 */
public final class Medias {

    ///////////////
    // ATTRIBUTS //
    ///////////////
    private ArrayList<Media> medias = new ArrayList<>();
    private ArrayList<Thumbnail> thumbnails = new ArrayList<>();

    private UploadPanel theView;
    private UplPanCon theController;

    protected String url;
    protected String api;

    /**
     * Implementation of the NEW Upload button listener
     */
    class NewUploadButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            theController.setNeedRefresh(true);
            theController.setRefreshType("loadUploadPanel");

        }
    }

    ////////////////////
    // NESTED CLASSES //
    ////////////////////
    /**
     * SwingWorker threading agent for thumbnails creation
     */
    public class ThumbnailsWorker extends SwingWorker<ArrayList<Thumbnail>, Thumbnail> {

        public ThumbnailsWorker() {
            // Init
        }

        @Override
        public ArrayList<Thumbnail> doInBackground() throws Exception {

            ArrayList<Thumbnail> generatedThumbnails = new ArrayList<>();

            // Task
            medias.forEach(media -> {

                Thumbnail thumbnail = thumbnails.get(media.getIndex());

                // is the thumbnail generated
                if (thumbnail.getIcon() == null) {
                    System.out.println("\n_Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is null --> starting creating one");

                    try {

                        thumbnail = createThumbnail(media);

                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }

                    publish(thumbnail);

                    generatedThumbnails.add(thumbnail);

                } else {
                    System.out.println("\n_Thumbnail n°" + media.getIndex() + "(" + thumbnail.getIndex() + ") is already there");

                    thumbnail = thumbnails.get(media.getIndex());

                    thumbnail.setIndex(media.getIndex());

                    publish(thumbnail);

                    generatedThumbnails.add(thumbnail);

                }

            });

            // Task end
            return generatedThumbnails;

        }

        @Override
        public void process(List<Thumbnail> chunks) {

            chunks.forEach(thumbnail -> {

                theView.addThumbnail(thumbnail);

                System.out.println("\n_asking for reload from model");

                theController.setNeedRefresh(true);
                theController.setRefreshType("reloadUploadPanel");

            });

        }

        @Override
        protected void done() {

            ArrayList<Thumbnail> result = new ArrayList<>();

            try {

                result = get();

            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace(System.err);
            }

            thumbnails = result;

            System.out.println("_Task finished");
        }

    }

    /**
     * SwingWorker threading agent for upload process
     */
    public class UploadWorker extends SwingWorker<String, ProgressPair> {

        public UploadWorker() {
            // Init
        }

        @Override
        public String doInBackground() throws Exception {

            String responseBody = null;

            // Task
            // CONNEXION API
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = null;
            try {
                sslcontext = SSLContexts.custom()
                        .loadTrustMaterial(new TrustSelfSignedStrategy())
                        .build();
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
                ex.printStackTrace(System.err);
            }

            // Allow TLSv1.2 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            // Building client
            try (CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build()) {

                HttpPost httpPost = new HttpPost("https://" + url + "/service.php?urlaction=upload");

                MultipartEntityBuilder mulitEntiBuilder = MultipartEntityBuilder.create();

                mulitEntiBuilder.addPart("api_key", new StringBody(api, ContentType.TEXT_PLAIN));

                medias.forEach(media -> {
                    MyFileBody fileBody = new MyFileBody(media.getFile());

                    fileBody.setListener(new IStreamListener() {

                        int progress = 0;
                        double send = 0;
                        double size = media.getFile().length();

                        @Override
                        public void counterChanged(int delta) {

                            send += delta;
                            progress = (int) (send / size * 100);

//                            System.out.println("-------------------------------------------------");
//                            System.out.println(media.getFile().getName() + " - delta : " + delta);
//                            System.out.println(media.getFile().getName() + " - send : " + send);
//                            System.out.println(media.getFile().getName() + " - size : " + size);
//                            System.out.println(media.getFile().getName() + " - progress : " + progress);
                            ProgressPair progressPair = new ProgressPair(media.getIndex(), progress);

                            publish(progressPair);

                        }
                    });

                    mulitEntiBuilder.addPart("media[]", fileBody);

                });

                HttpEntity formEntity = mulitEntiBuilder.build();

                httpPost.setEntity(formEntity);

                // Executing and getting the response
                System.out.println("Executing request " + httpPost.getRequestLine());

                try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        System.out.println("Response content length: " + resEntity.getContentLength());
                        responseBody = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);

                    }

                    EntityUtils.consume(resEntity);

                    // Task end
                }

            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }

            return responseBody;

        }

        @Override
        public void process(List<ProgressPair> chunks) {

            chunks.forEach(progress -> {

                theView.setProgress(progress);

                System.out.println("\n_asking for reload from model");

                theController.setNeedRefresh(true);
                theController.setRefreshType("reloadUploadPanel");

            });

            if (theView.isEveryProgress100()) {

                theView.waitingPopup();

            }

        }

        @Override
        protected void done() {

            try {

                String result = get();

                System.out.println("----------------------------------------");
                System.out.println(result);
                System.out.println("----------------------------------------");

                System.out.println("_Task finished");

                theView.closeWaitingPopup();
                theView.popup("Upload terminé avec succès !\n"
                        + "Les fichiers ont été envoyés au serveur pour traitement.");

                System.out.println("!!! asking for but switch from model");
                theView.switchUploadButton(new NewUploadButtonListener());

                System.out.println("!!! asking for reload from model");
                theController.setNeedRefresh(true);
                theController.setRefreshType("reloadUploadPanel");

            } catch (InterruptedException | ExecutionException | ParseException ex) {
                ex.printStackTrace(System.err);
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
     * @param theController
     * @param api_key
     * @param url
     */
    public Medias(UploadPanel theView, UplPanCon theController, String api_key, String url) {

        this.theView = theView;
        this.theController = theController;

        this.api = api_key;
        this.url = url;
    }

    /**
     * creating medias thumbnails in a separate thread
     *
     * @param media
     * @return
     * @throws java.io.FileNotFoundException
     */
    public Thumbnail createThumbnail(Media media) throws FileNotFoundException, IOException {

        // what type of file is it
        String mimetype = new MimetypesFileTypeMap().getContentType(media.getFile());
        System.out.println("_mimetype = " + mimetype);

        // Fixing missing MIME type
        if ("application/octet-stream".equals(mimetype)) {

            String extension = FilenameUtils.getExtension(media.getFile().getName());

            MimeTypesFixer fixer = new MimeTypesFixer();

            try {
                mimetype = fixer.getMap().get(extension).toString();
                System.out.println("_fixed missing mimetype = " + mimetype);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.out.println("_unable to fix MIME type, keeping: " + mimetype);
            }
        }

        String type = mimetype.split("/")[0];

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        switch (type) {

            case "image":

                System.out.println("_it's an image, starting thumbnail creation");

                // Image scaling without loading into memory
                // https://stackoverflow.com/questions/10817597/java-image-scaling-without-loading-the-whole-image-into-memory
                FileInputStream fin = new FileInputStream(media.getFile());

                ImageInputStream iis = ImageIO.createImageInputStream(fin);

                Iterator iter = ImageIO.getImageReaders(iis);
                if (!iter.hasNext()) {
                    break;
                }

                ImageReader reader = (ImageReader) iter.next();

                ImageReadParam params = reader.getDefaultReadParam();

                reader.setInput(iis, true, true);

                // Getting intermediate image with size divided by 4
                params.setSourceSubsampling(4, 4, 0, 0);

                img = reader.read(0, params);

                break;

            case "video":
                // If it's a video
                System.out.println("_it's a video, starting thumbnail creation");

                try {
                    // Get the first keyframe / frame

                    Picture picture = FrameGrab.getFrameFromFile(media.getFile(), 1);

                    //for JDK (jcodec-javase)
                    img = AWTUtil.toBufferedImage(picture);

                } catch (IOException | JCodecException e) {

                    e.printStackTrace(System.err);

                    // TODO: if unable to get keyFrame -> put generic video logo
                    BufferedImage fileLogo = ImageIO.read(new FileInputStream(Global.getWorkingDirPrefix() + "resources/img/icons8-video-100.png"));

                    img = fileLogo;

                }

                break;

            default:
                // Else --> generic file icon
                System.out.println("_it's any other file type, putting generic file icon");

                BufferedImage fileLogo = ImageIO.read(new FileInputStream(Global.getWorkingDirPrefix() + "resources/img/icons8-file-240.png"));

                img = fileLogo;

                break;
        }

        // Determining final Width and Height :
        Dimension dim = new Dimension(img.getWidth(), img.getHeight());

        Dimension newDim = getScaledDimension(dim, new Dimension(100, 100));

        Image newImg = img.getScaledInstance((int) newDim.getWidth(), (int) newDim.getHeight(), Image.SCALE_SMOOTH);


        ImageIcon icon = new ImageIcon(newImg);

        Thumbnail genThumb = new Thumbnail(media.getIndex(), icon);

        System.out.println("_creating thumbnail: " + media.getIndex());

        // Fin
        return genThumb;

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

    ///////////////////////
    // GETTERS / SETTERS //
    ///////////////////////
    public void addMedia(File file) {

        Integer index;

        if (this.medias.isEmpty()) {
            index = 0;
            System.out.println("_medias is empty: index = " + index);
        } else {
            index = this.medias.size();
            System.out.println("_medias is not empty: index = " + index);
        }

        Media media = new Media(index, file);

        this.medias.add(media);

        Thumbnail thumbnail = new Thumbnail(index);

        this.thumbnails.add(thumbnail);

    }

    public void sortAllByIndex() {

        this.medias.sort(Comparator.comparing(Media::getIndex));
        this.thumbnails.sort(Comparator.comparing(Thumbnail::getIndex));

    }

    public ArrayList<Media> getMedias() {
        return this.medias;
    }

    public void dumpMedia(int index) {

        System.out.println("_dumping media n°" + index);

        // ! Might cause bugs (needs to be tested)
        this.medias.remove(index);
        this.thumbnails.remove(index);

        // Fixing indexes
        medias.forEach(media -> {
            if (media.getIndex() > index) {
                media.setIndex(media.getIndex() - 1);
            }
        });

        thumbnails.forEach(thumbnail -> {
            if (thumbnail.getIndex() > index) {
                thumbnail.setIndex(thumbnail.getIndex() - 1);
            }
        });

    }

    public void dumpMedias() {

        System.out.println("_dumping all medias");

        this.medias.clear();
        this.thumbnails.clear();

    }

}
