package com.hysens.hermes.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hysens.hermes.common.exception.HermesException;
import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.Partner;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.PartnerRepository;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.service.message.MessageServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/messenger")
public class MessengerController {
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public SimpleMessageService simpleMessageService;
    @Autowired
    public PartnerRepository partnerRepository;

    @PostMapping("/whatsapp/login")
    public void loginWhatsapp(@RequestBody long partnerId) {
        Partner partner = partnerRepository.findById(partnerId).get();
        new MessageServiceFactory().from(MessengerEnum.WHATSAPP).loginInMessenger(partner);
    }

    @PostMapping("/telegram/login")
    public void loginTelegram(@RequestBody long partnerId) {
        Partner partner = partnerRepository.findById(partnerId).get();
        new MessageServiceFactory().from(MessengerEnum.TELEGRAM).loginInMessenger(partner);
    }

    @GetMapping("/telegram/logout")
    public String tgLogout() {
        return new MessageServiceFactory().from(MessengerEnum.TELEGRAM).logout();
    }

    @GetMapping("/whatsapp/logout")
    public String waLogout() {
        return new MessageServiceFactory().from(MessengerEnum.WHATSAPP).logout();
    }

    @GetMapping("/whatsapp/login/qr")
    public ResponseEntity<byte[]> getWaQr() throws WriterException, IOException {
        String response = new MessageServiceFactory().from(MessengerEnum.WHATSAPP).getQR();
        if (!response.equals("Logged in WhatsApp") && !response.isEmpty()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(response, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(pngData);
        }
        return ResponseEntity.accepted().body(null);
    }

    @GetMapping("/telegram/login/qr")
    public ResponseEntity<byte[]> getTgQr() throws WriterException, IOException {
        String response = new MessageServiceFactory().from(MessengerEnum.TELEGRAM).getQR();
        //simpleMessageService.setTelegramIdByPhone(489214541, "380683909142");
        if (response.contains("tg")) {
            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(response, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(pngData);
        }
        return ResponseEntity.accepted().body(null);
    }

    @GetMapping("/telegram/islogined/{id}")
    public boolean isTelegramLogined(@PathVariable("id") long partnerId) {
        Partner partner = partnerRepository.findById(partnerId).get();
        return new MessageServiceFactory().from(MessengerEnum.TELEGRAM).isMessengerLogined(partner.getPhone());
    }

    @GetMapping("/whatsapp/islogined/{id}")
    public boolean isWhatsappLogined(@PathVariable("id") long partnerId) {
        Partner partner = partnerRepository.findById(partnerId).get();
        return new MessageServiceFactory().from(MessengerEnum.WHATSAPP).isMessengerLogined(partner.getPhone());
    }

    @PostMapping("/contacts")
    public void addContacts(@RequestBody List<Client> clients) {
        try {
            clientRepository.saveAll(clients);
        } catch (Exception e) {
            throw new HermesException("DB_EXCEPTION", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
            @RequestParam long partnerId,
            @RequestParam(required = false) String chatStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String lastMessage,
            @RequestParam(required = false) String clientName
    ) {
        MessageStatusEnum messageStatusEnum = null;
        if (chatStatus != null && !chatStatus.equals("all")) {
            messageStatusEnum = MessageStatusEnum.valueOf(chatStatus.toUpperCase());
        }

        Pageable pageable;
        if (size == 0) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(page, size);
        }
        return clientRepository.findAllByCriteria(partnerId, messageStatusEnum, phone, lastMessage, clientName, pageable);
    }

}
