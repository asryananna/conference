package com.example.conference.controller;

import com.example.conference.controller.dto.room.RoomDto;
import com.example.conference.controller.dto.room.RoomResponseDto;
import com.example.conference.controller.dto.room.RoomUpdateDto;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.service.RoomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private static final Logger LOGGER = LogManager.getLogger(RoomController.class);

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping(value = "/check")
    public String checkStatus() {
        return "OK Room";
    }

    @PostMapping
    public ResponseEntity<RoomResponseDto> addRoom(@Valid @RequestBody RoomDto roomDto) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RoomController.addRoom method has been called");
        }

        RoomResponseDto roomResponseDto = roomService.saveRoom(roomDto);
        if (roomResponseDto != null) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(roomResponseDto.getId())
                    .toUri();

            LOGGER.info(String.format("A new room has been created: %s", location));
            return ResponseEntity.created(location).body(roomResponseDto);
        }
        throw new InvalidInputException("Can not create the document relevant to that data.");
    }

    @PatchMapping(value = "/{roomId}")
    public ResponseEntity<RoomResponseDto> updateRoom(@RequestBody RoomUpdateDto roomUpdateDto,
                                                      @PathVariable("roomId") String roomId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RoomController.updateRoom method has been called");
        }

        RoomResponseDto roomResponseDto = roomService.updateRoom(roomUpdateDto, roomId);
        if (roomResponseDto != null) {
            return new ResponseEntity<>(roomResponseDto, HttpStatus.OK);
        }

        throw new InvalidInputException("Can not update the document relevant to that data.");
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RoomController.getAllRooms method has been called");
        }

        List<RoomResponseDto> roomResponses = roomService.getAllRooms();
        if (roomResponses.isEmpty()) {
            LOGGER.warn("There is no any room to return");
            return new ResponseEntity<>(roomResponses, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roomResponses, HttpStatus.OK);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<RoomResponseDto>> checkRoomsAvailability(@RequestParam(value = "seats") Integer seats,
                                                                        @RequestParam(value = "eventDate")
                                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime eventDate) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RoomController.checkRoomsAvailability method has been called");
        }

        List<RoomResponseDto> roomResponses = roomService.checkRoomsAvailabilityByRequestedProp(seats, eventDate);
        if (roomResponses.isEmpty()) {
            LOGGER.warn(String.format("There is no any room to book by requested data: seats%d, eventDate: %s", seats, eventDate));
            return new ResponseEntity<>(roomResponses, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roomResponses, HttpStatus.OK);
    }

    @PatchMapping("/book/{roomId}")
    public ResponseEntity<RoomResponseDto> bookRoomForConference(@RequestHeader(value = "conferenceId") String conferenceId,
                                                                 @RequestParam(value = "roomId") String roomId) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RoomController.checkRoomAvailability method has been called");
        }

        RoomResponseDto roomResponse = roomService.bookRoomByConferenceId(roomId, conferenceId);
        if (roomResponse == null) {
            LOGGER.info(String.format("The room is no availability to book by requested data: conferenceId: %s", conferenceId));
            return new ResponseEntity<>(roomResponse, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roomResponse, HttpStatus.OK);
    }
}
