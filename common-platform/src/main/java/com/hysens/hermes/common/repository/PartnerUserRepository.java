package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.PartnerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerUserRepository extends JpaRepository<PartnerUser, Long> {
}
