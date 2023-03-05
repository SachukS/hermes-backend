package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.Client;
import com.hysens.hermes.common.model.enums.MessageStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByPhone(String phone);

    Client findByTelegramId(long id);

    @Query("SELECT cl FROM Client cl JOIN cl.lastMessage message WHERE message.messageStatus = :status")
    List<Client> findAllByMessageStatus(MessageStatusEnum status);

    @Query("SELECT cl " +
            "FROM Client cl " +
            "LEFT JOIN cl.lastMessage message " +
            "WHERE (:status IS NULL OR message.messageStatus = :status) " +
            "AND (:phoneNumber IS NULL OR cl.phone LIKE CONCAT('%', :phoneNumber, '%')) " +
            "AND (:text IS NULL OR message.message LIKE CONCAT('%', :text, '%')) " +
            "AND (:name IS NULL OR cl.name LIKE CONCAT('%', :name, '%')) "
    )
    Page<Client> findAllByCriteria(@Param("status") MessageStatusEnum status, @Param("phoneNumber") String phoneNumber,
                                   @Param("text") String text, @Param("name") String name, Pageable pageable);

}
