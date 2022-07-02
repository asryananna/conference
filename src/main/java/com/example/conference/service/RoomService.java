package com.example.conference.service;

import com.example.conference.controller.dto.room.RoomDto;
import com.example.conference.controller.dto.room.RoomResponseDto;
import com.example.conference.controller.dto.room.RoomUpdateDto;
import com.example.conference.dao.model.Conference;
import com.example.conference.dao.model.Room;
import com.example.conference.dao.model.RoomAvailability;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.exception.DocumentNotFoundException;
import com.example.conference.exception.InvalidInputException;
import com.example.conference.util.enumeration.RoomStatus;
import com.example.conference.util.mapper.CommonMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonTimestamp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class RoomService {
    private static final Logger LOGGER = LogManager.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    private final ConferenceRepository conferenceRepository;

    private final CommonMapper commonMapper;

    public RoomService(RoomRepository roomRepository, ConferenceRepository conferenceRepository, CommonMapper commonMapper) {
        this.roomRepository = roomRepository;
        this.conferenceRepository = conferenceRepository;
        this.commonMapper = commonMapper;
    }

    public RoomResponseDto saveRoom(RoomDto roomDto) {
        Room room = roomRepository.save(commonMapper.dtoToDao(roomDto));
        return commonMapper.daoToRoomResponseDto(room);
    }

    public RoomResponseDto updateRoom(RoomUpdateDto roomUpdateDto, String roomId) {
        Optional<Room> roomById = roomRepository.findById(roomId);
        if (roomById.isPresent()) {
            Room room = roomById.get();
            commonMapper.updateRoom(roomUpdateDto, room);
            roomRepository.save(room);
            return commonMapper.daoToRoomResponseDto(room);
        } else {
            throw new DocumentNotFoundException("No Conference found for id: " + roomId);
        }
    }

    public RoomResponseDto bookRoomByEventDate(String roomId, Integer requestedSeatsCount, LocalDateTime eventDate) {
        Room room = roomRepository.findByIdAndSeatsCountIsGreaterThanEqual(roomId, requestedSeatsCount)
                .orElseThrow(() -> new DocumentNotFoundException(
                        String.format("Could not find the room with requested properties. Id:%s, seats count:%d, status:%s",
                                roomId, requestedSeatsCount, RoomStatus.FREE)));

        checkDateAvailability(room, eventDate);
        bookRoom(room, eventDate);
        return commonMapper.daoToRoomResponseDto(room);
    }

    public RoomResponseDto bookRoomByConferenceId(String roomId, String conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new DocumentNotFoundException("There is no conference with id:" + conferenceId));

        Room room = roomRepository.findByIdAndSeatsCountIsGreaterThanEqual(roomId, conference.getRequestedSeatsCount())
                .orElseThrow(() -> new DocumentNotFoundException(
                        String.format("Could not find the room with requested properties. Id:%s, seats count:%d, status:%s",
                                roomId, conference.getRequestedSeatsCount(), RoomStatus.FREE)));

        LocalDateTime eventualDateTime = commonMapper.fromBsonTimestamp(conference.getEventDate());

        checkDateAvailability(room, commonMapper.fromBsonTimestamp(conference.getEventDate()));
        if (conference.getRoom() != null) {
            cancelBooking(conference.getRoom(), conference.getEventDate());
        }
        bookRoom(room, eventualDateTime);

        conference.setRoom(room);
        conferenceRepository.save(conference);

        return commonMapper.daoToRoomResponseDto(room);
    }

    public List<RoomResponseDto> checkRoomsAvailabilityByRequestedProp(Integer requestedSeatsCount, LocalDateTime eventDate) {
        List<Room> roomList = roomRepository.findBySeatsCountIsGreaterThanEqual(requestedSeatsCount);
        if (roomList.isEmpty()) {
            throw new DocumentNotFoundException(
                    String.format("Could not find any room with requested properties. seats count:%d, status:%s",
                            requestedSeatsCount, RoomStatus.FREE));
        }

        List<RoomResponseDto> availableRooms = new ArrayList<>();
        for (Room room : roomList) {
            try {
                if (checkDateAvailability(room, eventDate)) {
                    availableRooms.add(commonMapper.daoToRoomResponseDto(room));
                }
            } catch (InvalidInputException exception) {
                LOGGER.warn(String.format("The room with id: %s is no available for the date: %s", room.getId(), eventDate));
            }
        }

        return availableRooms;
    }

    public List<RoomResponseDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return commonMapper.daoListToRoomResponseDtoList(rooms);
    }

    protected RoomResponseDto bookRoom(Room room, LocalDateTime bookDate) {
        room.getRoomAvailability().add(new RoomAvailability(RoomStatus.BOOKED.name(), commonMapper.toBsonTimestamp(bookDate)));
        roomRepository.save(room);
        return commonMapper.daoToRoomResponseDto(room);
    }

    private boolean checkDateAvailability(Room room, LocalDateTime eventDate) {
        if (room.getRoomAvailability() != null) {
            for (RoomAvailability roomAvailability : room.getRoomAvailability()) {
                if (roomAvailability.getEventDate().equals(commonMapper.toBsonTimestamp(eventDate))) {
                    throw new InvalidInputException(String.format("Requested room has been already booked for the date: status of the room for %s is %s",
                            eventDate, roomAvailability.getStatus()));
                }
            }
        }
        return true;
    }

    public void cancelBooking(Room room, BsonTimestamp bookDate) {
        RoomAvailability roomAvailability = room.getRoomAvailability()
                .stream()
                .filter(availability -> availability.getEventDate().equals(bookDate))
                .findFirst()
                .orElseThrow(() -> new DocumentNotFoundException("There is no booking for the date"));

        room.getRoomAvailability().remove(roomAvailability);
        roomRepository.save(room);
    }
}
