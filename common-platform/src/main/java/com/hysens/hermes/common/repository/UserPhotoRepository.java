package com.hysens.hermes.common.repository;

import com.hysens.hermes.common.model.User;
import com.hysens.hermes.common.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
}
