package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.SimpleMessage;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleMessageRepository extends JpaRepository<SimpleMessage, Long> {
    List<SimpleMessage> findAllByClientIdOrderByCreatedDate(long clientId);

    List<SimpleMessage> findAllByClientIdAndMessageStatus(long clientId, MessageStatusEnum statusEnum);

    SimpleMessage findByMessageSpecId(String specId);

    SimpleMessage findFirstBySenderPhone(String phone);

    SimpleMessage findById(long id);

}
