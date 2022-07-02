package com.example.conference.controller.dto.error;

import com.example.conference.util.enumeration.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private String code;

    private String title;

    private String message;

    private String timeStamp;

    private ErrorType type;
}