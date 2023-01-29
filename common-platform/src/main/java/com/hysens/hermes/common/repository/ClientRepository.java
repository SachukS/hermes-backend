package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByPhone(String phone);
    Client findByTelegramId(long id);
    @Query("SELECT cl FROM Client cl JOIN cl.lastMessage message WHERE message.messageStatus = :status")
    List<Client> findAllByMessageStatus(MessageStatusEnum status);
}
