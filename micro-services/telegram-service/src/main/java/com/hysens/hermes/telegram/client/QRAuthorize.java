package com.hysens.hermes.telegram.client;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.util.Hashtable;

public class QRAuthorize {
    public static Image getQr(String url) {
        int width = 40;
        int height = 40;
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, qrParam);
            return generateQRCodeImage(bitMatrix);
        } catch (WriterException ex) {
            throw new IllegalStateException("Can't encode QR code", ex);
        }
    }
    public static Image generateQRCodeImage(BitMatrix matrix) {
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

}
