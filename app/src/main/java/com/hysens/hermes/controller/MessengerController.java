package com.hysens.hermes.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.service.message.MessageServiceFactory;
import com.hysens.hermes.service.message.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/messenger")
public class MessengerController {
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public SimpleMessageService simpleMessageService;

    @PostMapping("/whatsapp/login")
    public void loginWhatsapp() {
        new MessageServiceFactory().from(Messenger.WHATSAPP).loginInMessenger(simpleMessageService);
    }

    @PostMapping("/telegram/login")
    public void loginTelegram() {
        new MessageServiceFactory().from(Messenger.TELEGRAM).loginInMessenger(simpleMessageService);
    }

    @GetMapping("/telegram/logout")
    public String tgLogout() {
        return new MessageServiceFactory().from(Messenger.TELEGRAM).logout();
    }

    @GetMapping("/whatsapp/logout")
    public String waLogout() {
        return new MessageServiceFactory().from(Messenger.WHATSAPP).logout();
    }

    @GetMapping("/whatsapp/login/qr")
    public ResponseEntity<String> getWaQr() {
        String response = new MessageServiceFactory().from(Messenger.WHATSAPP).getQR();
        if (response.equals("Logged in WhatsApp"))
            return ResponseEntity.ok(response);
        return getQrResponse(response);
    }

    @GetMapping("/telegram/login/qr")
    public ResponseEntity<String> getTgQr() {
        String response = new MessageServiceFactory().from(Messenger.TELEGRAM).getQR();
        if (response.contains("tg")) {
            return getQrResponse(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/telegram/islogined")
    public boolean isTelegramLogined() {
        return new MessageServiceFactory().from(Messenger.TELEGRAM).isMessengerLogined();
    }

    @GetMapping("/whatsapp/islogined")
    public boolean isWhatsappLogined() {
        return new MessageServiceFactory().from(Messenger.WHATSAPP).isMessengerLogined();
    }

    @PostMapping("/contacts")
    public void addContacts(@RequestBody List<Client> clients) {
        clientRepository.saveAll(clients);
    }

    @PostMapping("/contacts/edit")
    public void editContact(@RequestBody Client client) {
        System.out.println(client);
        Optional<Client> existClient = clientRepository.findById(client.getId());
        if (existClient.isPresent()) {
            clientRepository.save(client);
        }
    }

    @GetMapping("/contacts/load")
    public Page<Client> getContacts(
            @RequestParam(required = false) String chatStatus,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String lastMessage,
            @RequestParam(required = false) String clientName
    ) {
        MessageStatusEnum messageStatusEnum = null;
        if (chatStatus != null && !chatStatus.equals("all")) {
            messageStatusEnum = MessageStatusEnum.valueOf(chatStatus.toUpperCase());
        }
        return clientRepository.findAllByCriteria(messageStatusEnum, phone, lastMessage, clientName, pageable);
    }

    private ResponseEntity<String> getQrResponse(String response) {
        byte[] imageData = generateQrCodeImageData(response);
        String base64ImageData = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        return new ResponseEntity<>(base64ImageData, headers, HttpStatus.OK);
    }

    private byte[] generateQrCodeImageData(String url) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
