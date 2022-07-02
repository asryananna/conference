package com.example.conference.exception;

import com.example.conference.controller.dto.error.ErrorResponseDto;
import com.example.conference.util.ErrorConstants;
import com.example.conference.util.enumeration.ErrorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandler {
   private static final Logger LOGGER = LogManager.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        ex.printStackTrace();

        final String message = ex.getCause() != null ? String.format("%s.%s", ex.getCause().getMessage(), ex.getMessage()) : ex.getMessage();
        LOGGER.error(String.format("Internal Server Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                ErrorConstants.INTERNAL_SERVER_ERROR_TITLE, message, LocalDateTime.now().toString(),
                ErrorType.API_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = { InvalidInputException.class })
    public ResponseEntity<ErrorResponseDto> handleInvalidInputException(InvalidInputException ex) {
        LOGGER.error(String.format("Invalid Input Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ErrorConstants.INVALID_INPUT_ERROR_TITLE, ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = { DocumentNotFoundException.class })    public ResponseEntity<ErrorResponseDto> handleNotFoundException(DocumentNotFoundException ex) {
        LOGGER.error(String.format("Document Not Found Exception: %s", ex.getMessage()), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(HttpStatus.NOT_FOUND.value()),
                ErrorConstants.NOT_FOUND_ERROR_TITLE, ex.getMessage(), LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
