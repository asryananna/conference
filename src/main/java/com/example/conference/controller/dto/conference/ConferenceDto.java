package com.example.conference.controller.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceDto {
    @NotNull
    private Integer requestedSeatsCount;

    @NotNull
    private LocalDateTime eventDate;

    private String roomId;

    @NotNull
    private String description;
}
