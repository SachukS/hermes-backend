package com.hysens.hermes.whatsapp.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.WhatsAppService;
import com.hysens.hermes.whatsapp.listener.WhatsAppListener;
import com.hysens.hermes.whatsapp.services.MessageSender;
import com.hysens.hermes.whatsapp.utils.CommunicateMethod;
import com.hysens.hermes.whatsapp.utils.QRAuthorize;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class WhatsAppLogin {
    public static JFrame QRCodeFrame = new JFrame();

    public static String WhatsappQr = "";

    public static void login(SimpleMessageService simpleMessageService) {
        String homePath = System.getProperty("user.home");
        Path web4jDirectory = Paths.get(homePath + "\\.whatsappweb4j");
        Whatsapp api;
        Whatsapp.Options options = Whatsapp.Options.builder()
                .qrHandler(onQRCode())
                .build();

        if (Files.exists(web4jDirectory)) {
            api = Whatsapp.lastConnection(options);
        } else {
            api = Whatsapp.newConnection(options);
        }
        //Whatsapp api = Whatsapp.newConnection();
        //Whatsapp api = Whatsapp.lastConnection();
        api.addListener(new WhatsAppListener(simpleMessageService));
        new MessageSender(api);

        api.connect().getNow(null);
    }

    public static QrHandler onQRCode() {
        return (qr) -> {
            WhatsappQr = qr;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (QRCodeFrame.isEnabled())
                QRCodeFrame.dispose();

            QRCodeFrame.setUndecorated(true);
//            CommunicateMethod authState = null;
//            try {
//                authState = WhatsAppService.communicateMethods.take();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            authState.setResult(qr);

            ImageIcon image = new ImageIcon(
                    QRAuthorize.generateQRCodeImage(createMatrix(qr, 256,5))
                            .getScaledInstance(256, 256,  Image.SCALE_SMOOTH));

            JLabel lbl = new JLabel(image);
            QRCodeFrame.getContentPane().add(lbl);
            QRCodeFrame.setSize(256, 256);

            int x = (screenSize.width - QRCodeFrame.getSize().width)/2;
            int y = (screenSize.height - QRCodeFrame.getSize().height)/2;

            QRCodeFrame.setLocation(x, y);
            QRCodeFrame.setVisible(true);
        };
    }

    private static BitMatrix createMatrix(String qr, int size, int margin) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            return writer.encode(qr, BarcodeFormat.QR_CODE, size, size, Map.of(EncodeHintType.MARGIN, margin, EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L));
        } catch (WriterException var4) {
            throw new UnsupportedOperationException("Cannot create qr code", var4);
        }
    }
}
