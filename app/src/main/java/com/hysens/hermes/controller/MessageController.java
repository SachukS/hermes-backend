package com.hysens.hermes.controller;

import com.hysens.hermes.common.exception.HermesException;
import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import com.hysens.hermes.common.model.enums.MessengerEnum;
import com.hysens.hermes.common.payload.request.MessageRequest;
import com.hysens.hermes.common.repository.ClientRepository;
import com.hysens.hermes.common.repository.SimpleMessageRepository;
import com.hysens.hermes.service.message.MessageServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    public static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    public SimpleMessageRepository simpleMessageRepository;
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public SimpMessagingTemplate messagingTemplate;

//    @Async("taskExecutor")
    @PostMapping("/send")
    public void postMessage(@RequestBody MessageRequest request) {
        List<MessengerEnum> messengerPriority = request.getMessengerPriority();

        String messageText = request.getSimpleMessage().getMessage();
        int segmentLength = 4096;
        int messageLength = messageText.length();
        int numSegments = (int) Math.ceil((double) messageLength / segmentLength);

        LOG.info("---------------------------------------NEW REQUEST-------------------------------------------");
        LOG.info("Trying to send message to number: " + request.getSimpleMessage().getReceiverPhone() + " using Telegram if chat with user exists");

        boolean isMessageSended = false;
        Client client = clientRepository.findByPhone(request.getSimpleMessage().getReceiverPhone());

        for (int i = 0; i < numSegments; i++) {
            int startIndex = i * segmentLength;
            int endIndex = Math.min(startIndex + segmentLength, messageLength);
            String messageSegment = messageText.substring(startIndex, endIndex);

            SimpleMessage messageToSend = new SimpleMessage();
            messageToSend.setClientId(request.getSimpleMessage().getClientId());
            messageToSend.setMessage(messageSegment);
            messageToSend.setReceiverPhone(request.getSimpleMessage().getReceiverPhone());
            messageToSend.setFromMe(true);
            messageToSend.setMessageStatus(MessageStatusEnum.PROCESSING);
            messageToSend = simpleMessageRepository.save(messageToSend);
            messagingTemplate.convertAndSend("/message/processing", messageToSend);

            for (MessengerEnum messenger : messengerPriority) {
                try {
                    isMessageSended = new MessageServiceFactory().from(messenger).sendIfChatWithUserExists(messageToSend);
                    if (isMessageSended) {
                        client.addConfirmedMessenger(messenger);
                        break;
                    }
                } catch (HermesException e) {
                    LOG.error(e.getMessage());
                }
            }

            if (!isMessageSended) {
                messageToSend.setMessageStatus(MessageStatusEnum.FAILED);
                messagingTemplate.convertAndSend("/message", messageToSend);
                LOG.error("Messege: " + messageToSend.getReceiverPhone() + " don't sended through in telegram and whatsapp");
            }
            client.setLastMessage(messageToSend);
            clientRepository.save(client);
        }
    }
    @GetMapping("/load/{id}")
    public List<SimpleMessage> getMessages(@PathVariable("id") long id) {
        return simpleMessageRepository.findAllByClientIdOrderByCreatedDate(id);
    }

    @PostMapping("/read")
    public void setMessageStatusRead(@RequestBody List<SimpleMessage> messages) {
        messages.forEach(message -> message.setMessageStatus(MessageStatusEnum.OPENED));
        simpleMessageRepository.saveAll(messages);
    }
}
