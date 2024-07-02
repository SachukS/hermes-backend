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
            "AND (cl.partnerId = :partnerId) " +
            "AND (:phoneNumber IS NULL OR CAST(cl.phone AS string) LIKE CONCAT('%', CAST(:phoneNumber AS string), '%')) " +
            "AND (:text IS NULL OR LOWER(CAST(message.message AS string)) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))) " +
            "AND (:name IS NULL OR LOWER(CAST(cl.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%'))) "
    )
    Page<Client> findAllByCriteria(@Param("partnerId") long partnerId, @Param("status") MessageStatusEnum status, @Param("phoneNumber") String phoneNumber,
                                   @Param("text") String text, @Param("name") String name, Pageable pageable);

}
