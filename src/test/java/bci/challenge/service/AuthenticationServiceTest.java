package bci.challenge.service;

import bci.challenge.dto.PhoneDTO;
import bci.challenge.dto.ResponseDTO;
import bci.challenge.dto.SignUpDTO;
import bci.challenge.entity.Phone;
import bci.challenge.entity.User;
import bci.challenge.exception.BciException;
import bci.challenge.repository.UserRepository;
import bci.challenge.security.JWTUtil;
import bci.challenge.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    private JWTUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    private static final String ENCODED_PASS = "$2a$10$m2A4JfZDNbYBRv6wu9CSbOhcHXR49tHS1M0ySAHKQZdzFIBdXUGqe";
    private static final String JWT = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZXJuYW5kb2FndXN0aW5jb2Njb0BnbWFpbC5jb20iLCJpc3MiOiJiY2kiLCJleHAiOjE2NzAwOTU3NTUsImlhdCI6MTY3MDAwOTM1NX0.8xMoL_EmbhOi2drCfFQaeiPujN7nk1rJnaQswGwqgdU";

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JWTUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        authenticationService = new AuthenticationServiceImpl(jwtUtil, passwordEncoder, userRepository);
    }

    @Test
    void signUp_ok_noPhones() throws BciException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setName("Fernando Cocco");
        signUpDTO.setEmail("test@mail.com");
        signUpDTO.setPassword("Password12");
        User persistedUser = new User();
        persistedUser.setId(UUID.randomUUID());
        persistedUser.setCreated(LocalDateTime.now());
        persistedUser.setLastLogin(LocalDateTime.now());
        persistedUser.setActive(true);
        persistedUser.setName(signUpDTO.getName());
        persistedUser.setEmail(signUpDTO.getEmail());
        persistedUser.setPassword(ENCODED_PASS);
        persistedUser.setLastToken(JWT);

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Password12")).thenReturn(ENCODED_PASS);
        when(jwtUtil.getEmailFromToken("test@mail.com")).thenReturn(JWT);
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        ResponseDTO responseDTO = authenticationService.signUp(signUpDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
        assertNotNull(responseDTO.getCreated());
        assertNotNull(responseDTO.getLastLogin());
        assertTrue(responseDTO.getIsActive());
        assertNotNull(responseDTO.getToken());
        assertEquals(JWT, responseDTO.getToken());
    }

    @Test
    void signUp_ok_withPhones() throws BciException {
        PhoneDTO phoneDTO = new PhoneDTO();
        phoneDTO.setCityCode(261);
        phoneDTO.setNumber(5123456);
        phoneDTO.setCountryCode("+54");
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setName("Fernando Cocco");
        signUpDTO.setEmail("test@mail.com");
        signUpDTO.setPassword("Password12");
        signUpDTO.setPhones(Collections.singletonList(phoneDTO));
        Phone phone = new Phone();
        phone.setCityCode(261);
        phone.setNumber(5123456L);
        phone.setCountryCode("+54");
        User persistedUser = new User();
        persistedUser.setId(UUID.randomUUID());
        persistedUser.setCreated(LocalDateTime.now());
        persistedUser.setLastLogin(LocalDateTime.now());
        persistedUser.setActive(true);
        persistedUser.setName(signUpDTO.getName());
        persistedUser.setEmail(signUpDTO.getEmail());
        persistedUser.setPassword(ENCODED_PASS);
        persistedUser.setLastToken(JWT);
        persistedUser.setPhones(Collections.singletonList(phone));

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Password12")).thenReturn(ENCODED_PASS);
        when(jwtUtil.getEmailFromToken("test@mail.com")).thenReturn(JWT);
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        ResponseDTO responseDTO = authenticationService.signUp(signUpDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
        assertNotNull(responseDTO.getCreated());
        assertNotNull(responseDTO.getLastLogin());
        assertTrue(responseDTO.getIsActive());
        assertNotNull(responseDTO.getToken());
        assertEquals(JWT, responseDTO.getToken());
    }

    @Test
    void signUp_saveError() throws BciException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setName("Fernando Cocco");
        signUpDTO.setEmail("test@mail.com");
        signUpDTO.setPassword("Password12");

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Password12")).thenReturn(ENCODED_PASS);
        when(jwtUtil.getEmailFromToken("test@mail.com")).thenReturn(JWT);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Duplicated primary key"));

        BciException exception = assertThrows(BciException.class, () -> authenticationService.signUp(signUpDTO));

        assertNotNull(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getHttpStatus());
        assertEquals("Duplicated primary key", exception.getMessage());
    }

    @Test
    void signUp_userAlreadyExists() {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setEmail("test@mail.com");
        signUpDTO.setPassword("Password12");

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(true);

        BciException exception = assertThrows(BciException.class, () -> authenticationService.signUp(signUpDTO));

        assertNotNull(exception);
        assertEquals(HttpStatus.CONFLICT.value(), exception.getHttpStatus());
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void login_ok() throws BciException {
        Phone phone = new Phone();
        phone.setCityCode(261);
        phone.setNumber(5123456L);
        phone.setCountryCode("+54");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setName("Fernando Cocco");
        user.setEmail("test@mail.com");
        user.setPassword(ENCODED_PASS);
        user.setLastToken(JWT);
        user.setPhones(Collections.singletonList(phone));

        when(jwtUtil.getEmailFromToken(JWT)).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(user);
        when(jwtUtil.generateToken("test@mail.com")).thenReturn(JWT);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseDTO response = authenticationService.login("Bearer " + JWT);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getCreated(), response.getCreated());
        assertEquals(user.getLastLogin(), response.getLastLogin());
        assertEquals(user.getLastToken(), response.getToken());
        assertEquals(user.isActive(), response.getIsActive());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void login_tokenExpired() throws BciException {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setName("Fernando Cocco");
        user.setEmail("test@mail.com");
        user.setPassword(ENCODED_PASS);
        user.setLastToken(JWT + "asd");
        user.setPhones(Collections.emptyList());

        when(jwtUtil.getEmailFromToken(JWT)).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(user);

        BciException exception = assertThrows(BciException.class, () -> authenticationService.login("Bearer " + JWT));

        assertNotNull(exception);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getHttpStatus());
        assertEquals("Token not valid", exception.getMessage());
    }

    @Test
    void login_saveError() throws BciException {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setName("Fernando Cocco");
        user.setEmail("test@mail.com");
        user.setPassword(ENCODED_PASS);
        user.setLastToken(JWT);
        user.setPhones(Collections.emptyList());

        when(jwtUtil.getEmailFromToken(JWT)).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com")).thenReturn(user);
        when(jwtUtil.generateToken("test@mail.com")).thenReturn(JWT);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Duplicated primary key"));

        BciException exception = assertThrows(BciException.class, () -> authenticationService.login("Bearer " + JWT));

        assertNotNull(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getHttpStatus());
        assertEquals("Duplicated primary key", exception.getMessage());
    }

}