package com.example.conference.controller.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomUpdateDto {
    private Integer number;

    private Integer seatsCount;
}
