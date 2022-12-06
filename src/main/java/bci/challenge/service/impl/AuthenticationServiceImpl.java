package bci.challenge.service.impl;

import bci.challenge.dto.PhoneDTO;
import bci.challenge.dto.ResponseDTO;
import bci.challenge.dto.SignUpDTO;
import bci.challenge.entity.Phone;
import bci.challenge.entity.User;
import bci.challenge.exception.BciException;
import bci.challenge.repository.UserRepository;
import bci.challenge.security.JWTUtil;
import bci.challenge.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO signUp(SignUpDTO signUpDTO) throws BciException {
        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new BciException(HttpStatus.CONFLICT.value(), "User already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        user.setLastToken(jwtUtil.generateToken(user.getEmail()));

        if (signUpDTO.getPhones() != null) {
            User finalUser = user;
            List<Phone> phones = signUpDTO.getPhones().stream()
                    .map(dto -> {
                        Phone phone = new Phone();
                        phone.setNumber(dto.getNumber());
                        phone.setCityCode(dto.getCityCode());
                        phone.setCountryCode(dto.getCountryCode());
                        phone.setUser(finalUser);
                        return phone;
                    }).collect(Collectors.toList());
            user.setPhones(phones);
        }

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new BciException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }

        return ResponseDTO.builder()
                .id(user.getId())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .token(user.getLastToken())
                .isActive(user.isActive())
                .build();
    }

    @Override
    public ResponseDTO login(String authorizationHeader) throws BciException {
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.getEmailFromToken(token);

        User user;
        try {
            user = userRepository.findByEmail(email);
            if (!token.equals(user.getLastToken())) {
                throw new BciException(HttpStatus.UNAUTHORIZED.value(), "Token not valid");
            }
            user.setLastLogin(LocalDateTime.now());
            user.setLastToken(jwtUtil.generateToken(user.getEmail()));
            user = userRepository.save(user);
        } catch (BciException e) {
            throw e;
        } catch (Exception e) {
            throw new BciException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }

        return ResponseDTO.builder()
                .id(user.getId())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .token(user.getLastToken())
                .isActive(user.isActive())
                .name(user.getName())
                .email(user.getEmail())
                .phones(mapPhonesToDto(user.getPhones()))
                .build();
    }

    private List<PhoneDTO> mapPhonesToDto(List<Phone> phones) {
        return phones.stream()
                .map(phone -> {
                    PhoneDTO dto = new PhoneDTO();
                    dto.setNumber(phone.getNumber());
                    dto.setCityCode(phone.getCityCode());
                    dto.setCountryCode(phone.getCountryCode());
                    return dto;
                }).collect(Collectors.toList());
    }

}
