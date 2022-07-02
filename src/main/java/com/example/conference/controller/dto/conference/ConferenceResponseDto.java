package com.example.conference.controller.dto.conference;

import com.example.conference.controller.dto.room.RoomResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceResponseDto {
    private String id;

    private Integer requestedSeatsCount;

    private String status;

    private LocalDateTime eventDate;

    private Set<ParticipantResponseDto> participants;

    private RoomResponseDto room;

    private String description;
}
