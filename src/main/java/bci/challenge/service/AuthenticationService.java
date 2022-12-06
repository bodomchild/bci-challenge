package bci.challenge.service;

import bci.challenge.dto.ResponseDTO;
import bci.challenge.dto.SignUpDTO;
import bci.challenge.exception.BciException;

public interface AuthenticationService {

    ResponseDTO signUp(SignUpDTO signUpDTO) throws BciException;

    ResponseDTO login(String authorizationHeader) throws BciException;

}
