package com.hysens.hermes.service.message;

import com.hysens.hermes.common.service.MessageService;
import com.hysens.hermes.common.service.SimpleMessageService;
import com.hysens.hermes.exception.MessageServiceNotFoundForMessengerException;
import com.hysens.hermes.telegram.service.TelegramService;
import com.hysens.hermes.whatsapp.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MessageServiceFactory {
    @Autowired
    public SimpleMessageService simpleMessageService;
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

//    @PostConstruct
//    public void loginToMessangers(){
//        from(Messenger.WHATSAPP).loginInMessenger(simpleMessageService);
//        from(Messenger.TELEGRAM).loginInMessenger(simpleMessageService);
//    }

}
