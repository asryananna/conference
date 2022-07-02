package com.example.conference.controller.dto.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    @NotNull
    private Integer number;

    @NotNull
    private Integer seatsCount;

    @NotNull
    private Integer floor;

    private AddressDto address;

    @JsonIgnore
    private List<RoomAvailabilityDto> roomAvailability;
}
