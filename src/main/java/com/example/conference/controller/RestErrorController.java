package com.example.conference.controller;

import com.example.conference.controller.dto.error.ErrorResponseDto;
import com.example.conference.util.enumeration.ErrorConstants;
import com.example.conference.util.enumeration.ErrorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ApiIgnore
@RestController
public class RestErrorController extends AbstractErrorController {
    private static final Logger LOGGER = LogManager.getLogger(RestErrorController.class);

    @Autowired
    public RestErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponseDto> handleErrors(HttpServletRequest request) {
        String errorMessage = "An error occurred during the request of URN or URN is not found. URN:" + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        LOGGER.error(errorMessage);

        HttpStatus status = getStatus(request);
        ErrorResponseDto errorResponse = new ErrorResponseDto(String.valueOf(status.value()),
                ErrorConstants.INVALID_REQUEST_ERROR_TITLE.name(), errorMessage, LocalDateTime.now().toString(),
                ErrorType.INVALID_REQUEST_ERROR);
        return new ResponseEntity<>(errorResponse, status);
    }
}