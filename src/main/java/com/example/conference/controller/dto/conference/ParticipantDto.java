package com.example.conference.controller.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Integer age;

    private String gender;

    private String residence;

    @NotNull
    private String companyName;

    @NotNull
    private String specialization;

    private String motivation;

    @NotNull
    private String invitedBy;
}
