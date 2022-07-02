package com.example.conference.controller.dto.room;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String addressLine1;

    private String addressLine2;

    private String ipAddressRange;

    private String timezone;

    private Double latitude;

    private Double longitude;
}
