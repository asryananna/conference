package com.example.conference.controller.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDto {
    private Long id;

    private String firstName;

    private String lastName;

    private Integer age;

    private String gender;

    private String residence;

    private String companyName;

    private String specialization;

    private String motivation;

    private String invitedBy;
}
