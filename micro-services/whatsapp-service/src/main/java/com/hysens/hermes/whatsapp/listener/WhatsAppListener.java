package com.hysens.hermes.whatsapp.listener;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.whatsapp.utils.QRAuthorize;
import it.auties.whatsapp.api.DisconnectReason;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.SocketEvent;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.model.action.Action;
import it.auties.whatsapp.model.info.MessageIndexInfo;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.standard.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class WhatsAppListener implements Listener {
    private JFrame QRCodeFrame = new JFrame();

    private SimpleMessageService simpleMessageService;
    public static final Logger LOG = LoggerFactory.getLogger(WhatsAppListener.class);

    public WhatsAppListener(SimpleMessageService simpleMessageService) {
        this.simpleMessageService = simpleMessageService;
    }
    public WhatsAppListener() {
    }
    //    @Override
//    public QrHandler onQRCode() {
//        return (qr) -> {
//            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//            if (QRCodeFrame.isEnabled())
//                QRCodeFrame.dispose();
//
//            QRCodeFrame.setUndecorated(true);
//
//            ImageIcon image = new ImageIcon(
//                    QRAuthorize.generateQRCodeImage(createMatrix(qr, 256,5))
//                            .getScaledInstance(256, 256,  Image.SCALE_SMOOTH));
//
//            JLabel lbl = new JLabel(image);
//            QRCodeFrame.getContentPane().add(lbl);
//            QRCodeFrame.setSize(256, 256);
//
//            int x = (screenSize.width - QRCodeFrame.getSize().width)/2;
//            int y = (screenSize.height - QRCodeFrame.getSize().height)/2;
//
//            QRCodeFrame.setLocation(x, y);
//            QRCodeFrame.setVisible(true);
//        };
//    }


    @Override
    public void onNewMessage(MessageInfo info) {
        if (!(info.message()
                .content() instanceof TextMessage textMessage)) {
            return;
        }
        if (!info.fromMe())
        {
            LOG.warn("Received new message: " + textMessage.text() + " from:" + info.senderJid().toPhoneNumber().substring(1));

            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setMessage(textMessage.text());
            simpleMessage.setSenderPhone(info.senderJid().toPhoneNumber().substring(1));
            simpleMessage.setFromMe(false);
            simpleMessage.setMessenger("Whatsapp");
            simpleMessage.setMessageStatus("Received");
            simpleMessageService.saveWithoutClientId(simpleMessage, 0L);
        }
        Listener.super.onNewMessage(info);
    }

    @Override
    public void onLoggedIn() {
        if (QRCodeFrame.isEnabled())
            QRCodeFrame.dispose();
        LOG.info("Logged in WhatsApp");
        Listener.super.onLoggedIn();
    }

    @Override
    public void onDisconnected(DisconnectReason reason) {
        LOG.warn("Whatsapp disconnected.");
        Listener.super.onDisconnected(reason);
    }

    @Override
    public void onAction(Action action, MessageIndexInfo messageIndexInfo) {
        LOG.error(messageIndexInfo.toString());
        Listener.super.onAction(action, messageIndexInfo);
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
