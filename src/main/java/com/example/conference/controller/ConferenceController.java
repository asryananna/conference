package com.example.conference.controller;

import com.example.conference.controller.dto.conference.ConferenceDto;
import com.example.conference.controller.dto.conference.ConferenceResponseDto;
import com.example.conference.controller.dto.conference.ConferenceUpdateDto;
import com.example.conference.controller.dto.conference.ParticipantDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.ConferenceService;
import com.example.conference.util.enumeration.ConferenceStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/conferences")
public class ConferenceController {

    private static final Logger LOGGER = LogManager.getLogger(ConferenceController.class);

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @GetMapping(value = "/check")
    public String checkStatus() {
        return "OK conference";
    }

    @PostMapping
    public ResponseEntity<ConferenceResponseDto> addConference(@Valid @RequestBody ConferenceDto conferenceDto) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.addConference method has been called");
        }

        ConferenceResponseDto conferenceResponseDto = conferenceService.saveConference(conferenceDto);
        if (conferenceResponseDto != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(conferenceResponseDto.getId())
                    .toUri();

            LOGGER.info(String.format("A new conference has been created: %s", location));
            return ResponseEntity.created(location).body(conferenceResponseDto);
        }
        throw new InvalidInputException("Can not create the document relevant to that data.");
    }

    @PatchMapping(value = "/{conferenceId}")
    public ResponseEntity<ConferenceResponseDto> updateConference(@Valid @RequestBody ConferenceUpdateDto conferenceUpdateDto,
                                                                  @PathVariable("conferenceId") String conferenceId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.updateConference method has been called");
        }

        ConferenceResponseDto conferenceResponseDto = conferenceService.updateConference(conferenceUpdateDto, conferenceId);
        if (conferenceResponseDto != null) {
            LOGGER.info("The conference has been updated successfully");
            return new ResponseEntity<>(conferenceResponseDto, HttpStatus.OK);
        }

        throw new InvalidInputException("Can not update the conference relevant to that data.");
    }

    @PatchMapping(value = "/cancel/{conferenceId}")
    public ResponseEntity<ConferenceResponseDto> cancelConference(@PathVariable("conferenceId") String conferenceId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.cancelConference method has been called");
        }

        ConferenceUpdateDto conferenceUpdateDto = new ConferenceUpdateDto();
        conferenceUpdateDto.setStatus(String.valueOf(ConferenceStatus.CANCELED));
        ConferenceResponseDto conferenceResponseDto = conferenceService.updateConference(conferenceUpdateDto, conferenceId);
        if (conferenceResponseDto != null) {
            LOGGER.info("The conference has been canceled successfully");
            return new ResponseEntity<>(conferenceResponseDto, HttpStatus.OK);
        }

        throw new InvalidInputException("Can not cancel the document relevant to that data.");
    }

    @GetMapping
    public ResponseEntity<List<ConferenceResponseDto>> getAllConferences() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.getAllConferences method has been called");
        }

        List<ConferenceResponseDto> conferenceResponses = conferenceService.getAllConferences();
        if (conferenceResponses.isEmpty()) {
            return new ResponseEntity<>(conferenceResponses, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(conferenceResponses, HttpStatus.OK);
    }

    @PostMapping(value = "/{conferenceId}/participants")
    public ResponseEntity<ConferenceResponseDto> addParticipant(@PathVariable("conferenceId") String conferenceId,
                                                                @Valid @RequestBody ParticipantDto participantDto) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.addParticipant method has been called");
        }

        ConferenceResponseDto conferenceResponseDto = conferenceService.addParticipant(participantDto, conferenceId);
        if (conferenceResponseDto != null) {
            LOGGER.info("The new participant has been canceled successfully");
            return new ResponseEntity<>(conferenceResponseDto, HttpStatus.CREATED);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }

    @DeleteMapping(value = "/{conferenceId}/participants/{participantId}")
    public ResponseEntity<ConferenceResponseDto> removeParticipant(@PathVariable("participantId") Long participantId,
                                                                   @PathVariable("conferenceId") String conferenceId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConferenceController.removeParticipant method has been called");
        }

        ConferenceResponseDto conferenceResponseDto = conferenceService.removeParticipant(participantId, conferenceId);
        if (conferenceResponseDto != null) {
            LOGGER.info("The conference has been removed successfully");
            return new ResponseEntity<>(conferenceResponseDto, HttpStatus.OK);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }
}
