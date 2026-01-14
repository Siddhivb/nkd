package com.kindhands.backend.controller;

import com.kindhands.backend.entity.OtpVerification;
import com.kindhands.backend.repository.OtpVerificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin
public class OtpController {

    private final OtpVerificationRepository otpRepo;

    public OtpController(OtpVerificationRepository otpRepo) {
        this.otpRepo = otpRepo;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpVerification request) {

        String mobile = request.getMobile();

        if (mobile == null || mobile.length() != 10) {
            return ResponseEntity.badRequest().body("Invalid mobile number");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setMobile(mobile);
        otpVerification.setOtp(otp);
        otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepo.deleteByMobile(mobile);
        otpRepo.save(otpVerification);

        System.out.println("OTP for " + mobile + " : " + otp);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerification request) {

        Optional<OtpVerification> otpOpt =
                otpRepo.findTopByMobileOrderByIdDesc(request.getMobile());

        if (otpOpt.isEmpty()) {
            return ResponseEntity.status(404).body("OTP not found");
        }

        OtpVerification savedOtp = otpOpt.get();

        if (LocalDateTime.now().isAfter(savedOtp.getExpiryTime())) {
            return ResponseEntity.badRequest().body("OTP expired");
        }

        if (!savedOtp.getOtp().equals(request.getOtp())) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        return ResponseEntity.ok("OTP verified successfully");
    }
}
