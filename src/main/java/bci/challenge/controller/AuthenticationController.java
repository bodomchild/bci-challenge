package bci.challenge.controller;

import bci.challenge.dto.ResponseDTO;
import bci.challenge.dto.SignUpDTO;
import bci.challenge.exception.BciException;
import bci.challenge.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDTO> signUp(@RequestBody @Valid SignUpDTO signUpDTO) throws BciException {
        ResponseDTO response = authenticationService.signUp(signUpDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws BciException {
        ResponseDTO response = authenticationService.login(authorizationHeader);
        return ResponseEntity.ok(response);
    }

}
