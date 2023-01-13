package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByPhone(String phone);
    Client findByTelegramId(long id);
}
