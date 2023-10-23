package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.OTPstore;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.exception.OTPException;
import com.kshrd.autopilot.exception.UserNotFoundException;
import com.kshrd.autopilot.exception.UsernameAlreadyExistsException;
import com.kshrd.autopilot.repository.OTPRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.entities.request.AuthenticationRequest;
import com.kshrd.autopilot.entities.request.ResetPasswordRequest;
import com.kshrd.autopilot.service.EmailService;
import com.kshrd.autopilot.service.UserService;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.util.CurrentUserUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Value("${error.url}")
    private String urlError;
    private static final int MIN_VALUE = 100_000;
    private static final int MAX_VALUE = 999_999;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OTPRepository otpRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, OTPRepository otpRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Username not found with username: " + username);
        }
        return userDetails;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username).toUserDto();

    }

    @Override
    public UserDto registration(AuthenticationRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new UsernameAlreadyExistsException("Username is token", "That Username is taken. Try another");
        } else if (userRepository.findUsersByEmail(request.getEmail()) != null) {
            throw new UsernameAlreadyExistsException("Email is already exist", "Email " + request.getEmail() + " is already exist");
        } else {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setGender(request.getGender());
            userRepository.save(user);
        }

        return userRepository.findByUsername(request.getUsername()).toUserDto();
    }

    public static int generateUniqueSixDigitNumber() {
        Set<Integer> generatedNumbers = new HashSet<>();
        Random random = new Random();

        while (true) {
            int candidate = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;

            if (generatedNumbers.add(candidate)) {
                return candidate;
            }
        }
    }

    @Override
    public void sendOTP(String username, HttpServletRequest request) throws MessagingException {
        User user = userRepository.findUsersByEmail(username);
        final int otp_code = generateUniqueSixDigitNumber();

        int port = 0;
        if (user == null) {
            throw new UserNotFoundException("User not found!", "This email not found!");
        }
        String appUrl =
                "http://" + request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath()+"/api/v1/auth/verifyOTP?otp="+otp_code;
        System.out.println(appUrl);
        //System.out.println(protocol+"://"+host+":"+String.valueOf(port));
//        Optional<OTPstore> otpOptional = otpRepository.findByUserId(user.getId());
//        if (otpOptional.isPresent()) {
//            OTPstore updateOtp = otpOptional.get();
//            updateOtp.setOtp_code(otp_code);
//            updateOtp.setCreated_at(LocalDateTime.now());
//            otpRepository.save(updateOtp);
//            emailService.sendOTPEmail(username, user.getUsername(), appUrl);
//        } else {
//            OTPstore otPstore = new OTPstore();
//            otPstore.setUser(user);
//            otPstore.setOtp_code(otp_code);
//            otPstore.setCreated_at(LocalDateTime.now());
//            otpRepository.save(otPstore);
//            emailService.sendOTPEmail(username, user.getUsername(), appUrl);
//        }

    }

    @Override
    public void verifyOTP(Integer otp) {
        LocalDateTime now = LocalDateTime.now();
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Optional<OTPstore> otPstore = otpRepository.findByUserId(user.getId());
        Integer expired = now.getMinute() - otPstore.get().getCreated_at().getMinute();
        if (expired > 4 && otp.equals(otPstore.get().getOtp_code())) {
            throw new OTPException("Expired OTP", "Your OTP is expired");
        } else if (!otp.equals(otPstore.get().getOtp_code())) {
            throw new OTPException("Incorrect OTP", "Your OTP is incorrect!");
        } else {
            OTPstore update_verify = otPstore.get();
            update_verify.setIs_verify(true);
            otpRepository.save(update_verify);
        }
    }

    @Override
    public UserDto resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findUsersByEmail(request.getEmail());
        Optional<OTPstore> otPstore = otpRepository.findByUserId(user.getId());
        if (!otPstore.get().getIs_verify()) {
            throw new AutoPilotException("OTP not yet verify", HttpStatus.FORBIDDEN,
                    urlError, "Please verify email with OTP");
        } else {
            String newPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(newPassword);
            userRepository.save(user);
        }
        return user.toUserDto();
    }

}
