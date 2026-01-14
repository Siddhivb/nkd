package com.kindhands.backend.repository;

import com.kindhands.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByMobileOrderByIdDesc(String mobile);

    @Transactional
    void deleteByMobile(String mobile);
}
