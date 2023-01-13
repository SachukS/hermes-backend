package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.SimpleMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleMessageRepository extends JpaRepository<SimpleMessage, Long> {
    List<SimpleMessage> findAllByClientId(long clientId);

    SimpleMessage findFirstBySenderPhone(String phone);

}
