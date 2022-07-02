package com.example.conference.service;

import com.example.conference.controller.dto.conference.ConferenceDto;
import com.example.conference.controller.dto.conference.ConferenceResponseDto;
import com.example.conference.controller.dto.conference.ConferenceUpdateDto;
import com.example.conference.controller.dto.conference.ParticipantDto;
import com.example.conference.controller.dto.room.RoomResponseDto;
import com.example.conference.dao.model.Conference;
import com.example.conference.dao.model.Participant;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.util.enumeration.ConferenceStatus;
import com.example.conference.util.mapper.CommonMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    private final RoomRepository roomRepository;

    private final RoomService roomService;

    private final SequenceGeneratorService sequenceGeneratorService;

    private final CommonMapper commonMapper;

    public ConferenceService(ConferenceRepository conferenceRepository, RoomRepository roomRepository, RoomService roomService, SequenceGeneratorService sequenceGeneratorService, CommonMapper commonMapper) {
        this.conferenceRepository = conferenceRepository;
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.commonMapper = commonMapper;
    }

    public ConferenceResponseDto saveConference(ConferenceDto conferenceDto) {
        Conference conference = commonMapper.dtoToDao(conferenceDto);
        conference.setStatus(ConferenceStatus.SCHEDULED.name());
        if (conferenceDto.getRoomId() != null && !conferenceDto.getRoomId().isEmpty()) {
            RoomResponseDto roomResponseDto = roomService.bookRoomByEventDate(conferenceDto.getRoomId(), conferenceDto.getRequestedSeatsCount(), conferenceDto.getEventDate());
            conference.setRoom(commonMapper.dtoToDao(roomResponseDto));
        }

        conferenceRepository.save(conference);
        return commonMapper.daoToDResponseDto(conference);

    }

    public ConferenceResponseDto updateConference(ConferenceUpdateDto conferenceUpdateDto, String conferenceId) {
        validateStatus(conferenceUpdateDto);

        Optional<Conference> byId = conferenceRepository.findById(conferenceId);
        if (byId.isPresent()) {
            Conference conference = byId.get();
            commonMapper.updateConference(conferenceUpdateDto, conference);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        } else {
            throw new DocumentNotFoundException("No Conference found for id: " + conferenceId);
        }
    }

    public List<ConferenceResponseDto> getAllConferences() {
        List<Conference> conferences = conferenceRepository.findAll();
        return commonMapper.daoListToConferenceResponseDtoList(conferences);
    }

    private void validateStatus(ConferenceUpdateDto conferenceUpdateDto) {
        if (conferenceUpdateDto.getStatus() != null && (conferenceUpdateDto.getStatus().isEmpty() || ConferenceStatus.findByName(conferenceUpdateDto.getStatus()) == null)) {
            throw new InvalidInputException(String.format("Given conference status is invalid:%s. Valid statuses are:%s",
                    conferenceUpdateDto.getStatus(), Arrays.toString(ConferenceStatus.values())));
        }
    }

    public ConferenceResponseDto addParticipant(ParticipantDto participantDto, String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("No Conference found for id: %sCould not add a participant for the conference", conferenceId)));

        if (ConferenceStatus.CANCELED.name().equals(conference.getStatus())) {
            throw new InvalidInputException("The conference has been canceled. Unable to add a participant: status: " + conference.getStatus());
        }

        if (conference.getParticipants().size() < conference.getRequestedSeatsCount()) {
            Participant participant = commonMapper.dtoToDao(participantDto);
            participant.setId(sequenceGeneratorService.generateSequence(Participant.SEQUENCE_NAME));
            conference.getParticipants().add(participant);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        }
        throw new InvalidInputException("There is no free space to add new participant. seats: " + conference.getRequestedSeatsCount());
    }

    public ConferenceResponseDto removeParticipant(Long participantId, String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException(
                String.format("No Conference found for id: %sCould not add a participant for the conference", conferenceId)));

        if (conference.getParticipants() != null) {
            Participant participantToRemove = conference.getParticipants()
                    .stream()
                    .filter(participant -> participantId.equals(participant.getId()))
                    .findFirst()
                    .orElseThrow(() -> new DocumentNotFoundException(String.format(
                            "Could not find a participant with id for the conference. conferenceId: %s, participantId:%d",
                            conferenceId, participantId)));

            conference.getParticipants().remove(participantToRemove);
            conferenceRepository.save(conference);
            return commonMapper.daoToDResponseDto(conference);
        }

        throw new DocumentNotFoundException("There is no registered participants for the conference. conferenceId: " + conference.getId());
    }
}
