package com.hysens.hermes.service.message;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.exception.MessageServiceNotFoundForMessengerException;
import com.hysens.hermes.telegram.TelegramService;
import com.hysens.hermes.whatsapp.WhatsAppService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class MessageServiceFactory {

    @NonNull
    public MessageService from(@NonNull Messenger messenger) {
        switch (messenger) {
            case TELEGRAM:
                return new TelegramService();

            case WHATSAPP:
                return new WhatsAppService();

            default:
                throw new MessageServiceNotFoundForMessengerException(messenger);
        }
    }

}
