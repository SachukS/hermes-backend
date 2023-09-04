package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.PartnerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerUserRepository extends JpaRepository<PartnerUser, Long> {
    Optional<PartnerUser> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
