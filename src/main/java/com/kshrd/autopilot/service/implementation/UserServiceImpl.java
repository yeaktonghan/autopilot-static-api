package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.ConfirmationEmail;
import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.OTPstore;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.exception.BadRequestException;
import com.kshrd.autopilot.exception.NotFoundException;
import com.kshrd.autopilot.repository.ConfirmationEmailRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final ConfirmationEmailRepository confirmationEmailRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, OTPRepository otpRepository, ConfirmationEmailRepository confirmationEmailRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
        this.confirmationEmailRepository = confirmationEmailRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByUsername(username);
        if (userDetails == null) {
            throw new AutoPilotException("Username not found", HttpStatus.NOT_FOUND,
                    urlError, "Username is not registered yet!");
        }
        return userDetails;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new AutoPilotException("Username not found", HttpStatus.NOT_FOUND,
                    urlError, "Username is not registered yet!");
        }
        return user.toUserDto();

    }

    @Override
    public UserDto registration(AuthenticationRequest request, HttpServletRequest requestSer) throws MessagingException {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new BadRequestException("Username is token", "That Username is taken. Try another");
        } else if (userRepository.findUsersByEmail(request.getEmail()) != null) {
            throw new BadRequestException("Email is already exist", "Email " + request.getEmail() + " is already exist");
        } else {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            ConfirmationEmail confirmationEmail = new ConfirmationEmail(user);
            confirmationEmailRepository.save(confirmationEmail);
           String appUrl ="http://localhost:5173/signin?token=" + confirmationEmail.getConfirmationToken();
            emailService.confirmEmail(request.getEmail(),appUrl);
        }

        return userRepository.findByUsername(request.getUsername()).toUserDto();
       // return  null;
    }

    @Override
    public UserDto confirmEmail(String emailtoken) {
        ConfirmationEmail token = confirmationEmailRepository.findByConfirmationToken(emailtoken);
        if(token != null)
        {
           Optional< User> user = userRepository.findById(token.getUser().getId());
            user.get().setEnabled(true);
            userRepository.save(user.get());
            return user.get().toUserDto();
        }else {
            throw new AutoPilotException("Not found",HttpStatus.NOT_FOUND,urlError,"Error: Couldn't verify email");
        }

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
            throw new NotFoundException("User not found!", "This email not found!");
        }
        String appUrl =
                "http://" + request.getServerName() +
                        ":" + request.getServerPort() +
                        request.getContextPath()+"/api/v1/auth/verifyOTP?otp="+otp_code;
       // System.out.println(protocol+"://"+host+":"+String.valueOf(port));
        Optional<OTPstore> otpOptional = otpRepository.findByUserId(user.getId());
        if (otpOptional.isPresent()) {
            OTPstore updateOtp = otpOptional.get();
            updateOtp.setOtp_code(otp_code);
            updateOtp.setCreated_at(LocalDateTime.now());
            otpRepository.save(updateOtp);
            emailService.sendOTPEmail(username, user.getUsername(), appUrl);
        } else {
            OTPstore otPstore = new OTPstore();
            otPstore.setUser(user);
            otPstore.setOtp_code(otp_code);
            otPstore.setCreated_at(LocalDateTime.now());
            otpRepository.save(otPstore);
            emailService.sendOTPEmail(username, user.getUsername(), appUrl);
      }

    }

    @Override
    public void verifyOTP(Integer otp) {
        LocalDateTime now = LocalDateTime.now();
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Optional<OTPstore> otPstore = otpRepository.findByUserId(user.getId());
        Integer expired = now.getMinute() - otPstore.get().getCreated_at().getMinute();
        if (expired > 4 && otp.equals(otPstore.get().getOtp_code())) {
            throw new NotFoundException("Expired OTP", "Your OTP is expired");
        } else if (!otp.equals(otPstore.get().getOtp_code())) {
            throw new NotFoundException("Incorrect OTP", "Your OTP is incorrect!");
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
