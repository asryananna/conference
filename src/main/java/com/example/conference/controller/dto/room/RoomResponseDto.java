package com.example.conference.controller.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    private String id;

    private Integer number;

    private Integer seatsCount;

    private Integer floor;

    private AddressResponseDto address;

    private List<RoomAvailabilityDto> roomAvailability;
}
