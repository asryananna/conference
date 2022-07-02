package com.example.conference.util.mapper;

import com.example.conference.controller.dto.conference.*;
import com.example.conference.controller.dto.room.*;
import com.example.conference.dao.model.*;
import org.bson.BsonTimestamp;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface CommonMapper {
    @Named("fromBsonTimestamp")
    default LocalDateTime fromBsonTimestamp(BsonTimestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getTime()), ZoneOffset.UTC);
    }

    @Named("toBsonTimestamp")
    default BsonTimestamp toBsonTimestamp(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return new BsonTimestamp((int) value.toEpochSecond(ZoneOffset.UTC), 1);
    }

    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "toBsonTimestamp")
    Conference dtoToDao(ConferenceDto conferenceDto);

    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "fromBsonTimestamp")
    @Named(value = "daoToConferenceDto")
    ConferenceResponseDto daoToDResponseDto(Conference conference);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "toBsonTimestamp")
    void updateConference(ConferenceUpdateDto conferenceUpdateDto, @MappingTarget Conference conference);

    @IterableMapping(qualifiedByName = "daoToConferenceDto")
    List<ConferenceResponseDto> daoListToConferenceResponseDtoList(List<Conference> conferences);

    Participant dtoToDao(ParticipantDto participantDto);

    ParticipantResponseDto daoToResponseDto(Participant participant);

    Room dtoToDao(RoomDto roomDto);

    @Named("daoToRoomResponseDto")
    RoomResponseDto daoToRoomResponseDto(Room room);

    @IterableMapping(qualifiedByName = "daoToRoomResponseDto")
    List<RoomResponseDto> daoListToRoomResponseDtoList(List<Room> rooms);

    Room dtoToDao(RoomResponseDto roomDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRoom(RoomUpdateDto roomUpdateDto, @MappingTarget Room room);

    @Named(value = "dtoToRoomDao")
    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "toBsonTimestamp")
    RoomAvailability dtoToDao(RoomAvailabilityDto roomAvailabilityDto);

    @IterableMapping(qualifiedByName = "dtoToRoomDao")
    List<RoomAvailability> dtoListToDaoList(List<RoomAvailabilityDto> roomAvailabilities);

    @Named(value = "daoToRoomAvailabilityDto")
    @Mapping(source = "eventDate", target = "eventDate", qualifiedByName = "fromBsonTimestamp")
    RoomAvailabilityDto daoToRoomDto(RoomAvailability roomAvailability);

    @IterableMapping(qualifiedByName = "daoToRoomAvailabilityDto")
    List<RoomAvailabilityDto> daoListToRoomAvailabilityDtoList(List<RoomAvailability> roomAvailabilities);

    Address dtoToDao(AddressDto addressDto);

    AddressResponseDto daoToDResponseDto(Address address);
}
