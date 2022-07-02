package com.example.conference.controller.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceUpdateDto {
    private Integer requestedSeatsCount;

    private String status;

    private LocalDateTime eventDate;

    private String description;
}
