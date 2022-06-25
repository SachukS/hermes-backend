package com.hysens.hermes.whatsapp.listener;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hysens.hermes.whatsapp.services.MessageSender;
import com.hysens.hermes.whatsapp.utils.QRAuthorize;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.WhatsappListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class WhatsAppListener implements WhatsappListener {
    private JFrame QRCodeFrame = new JFrame();

    @Override
    public QrHandler onQRCode() {
        return (qr) -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (QRCodeFrame.isEnabled())
                QRCodeFrame.dispose();

            QRCodeFrame.setUndecorated(true);

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

    @Override
    public void onLoggedIn() {
        if (QRCodeFrame.isEnabled())
            QRCodeFrame.dispose();
        System.out.println("LOGINNED");
        WhatsappListener.super.onLoggedIn();
    }

    @Override
    public void onDisconnected(boolean reconnect) {
        System.out.println("Whatsapp disconnected.");
        WhatsappListener.super.onDisconnected(reconnect);
    }

//    @Override
//    public void onContacts() {
//        System.out.println("onContacts");
//        WhatsappListener.super.onContacts();
//    }

    private static BitMatrix createMatrix(String qr, int size, int margin) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            return writer.encode(qr, BarcodeFormat.QR_CODE, size, size, Map.of(EncodeHintType.MARGIN, margin, EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L));
        } catch (WriterException var4) {
            throw new UnsupportedOperationException("Cannot create qr code", var4);
        }
    }
}
