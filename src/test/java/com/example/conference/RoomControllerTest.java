package com.example.conference;

import com.example.conference.constants.TestConstants;
import com.example.conference.dao.document.Conference;
import com.example.conference.dao.document.Room;
import com.example.conference.dao.repository.ConferenceRepository;
import com.example.conference.dao.repository.RoomRepository;
import com.example.conference.util.enumeration.ConferenceStatus;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.bson.BsonTimestamp;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RoomControllerTest {
    @Value("${api.local.url}")
    private String BASE_URI;

    @Value("${server.servlet.context-path}")
    private String ROOT;

    private static String URI;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ConferenceRepository conferenceRepository;

    @BeforeEach
    public void setup() {
        URI = String.format("%s%s%s%s", TestConstants.HTTP, BASE_URI, ROOT, TestConstants.API_ROOM);
    }

    @Test
    void testRoomCheckShouldReturnOk() {
        get(URI + "/check").then().assertThat().statusCode(HttpStatus.SC_OK);
    }

    @Test
    void testPostRoomShouldReturnCreated() throws JSONException {
        Integer count = 10;
        JSONObject roomParams = new JSONObject();
        roomParams.put("seatsCount", count);
        roomParams.put("number", count);
        roomParams.put("floor", count);

        given()
                .contentType(ContentType.JSON)
                .body(roomParams.toString())
                .log().all()
                .when()
                .post(URI)
                .then()
                .assertThat().statusCode(HttpStatus.SC_CREATED)
                .body("seatsCount", equalTo(count))
                .body("number", equalTo(count))
                .body("floor", equalTo(count))
                .log().all();
    }

    @Test
    void testPatchRoomShouldReturnOk() throws JSONException {
        Integer count = 10;

        Room room = Room.builder()
                .seatsCount(count)
                .number(count)
                .floor(count)
                .build();

        roomRepository.save(room);

        JSONObject roomParams = new JSONObject();
        roomParams.put("seatsCount", count + 1);

        given()
                .contentType(ContentType.JSON)
                .body(roomParams.toString())
                .log().all()
                .when()
                .patch(URI + "/" + room.getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .body("seatsCount", equalTo(count + 1))
                .log().all();
    }

    @Test
    void testPatchBookRoomShouldReturnOk() throws JSONException {
        Integer count = 10;
        Room room = Room.builder()
                .seatsCount(count)
                .number(count)
                .floor(count)
                .roomAvailability(new ArrayList<>())
                .build();

        roomRepository.save(room);

        int requestedSeatsCount = 1;
        Conference conference = Conference.builder()
                .eventDate(new BsonTimestamp(10, 100))
                .status(ConferenceStatus.SCHEDULED.name())
                .requestedSeatsCount(requestedSeatsCount)
                .build();

        conferenceRepository.save(conference);

        JSONObject roomParams = new JSONObject();
        roomParams.put("seatsCount", count + 1);

        given()
                .contentType(ContentType.JSON)
                .header("conferenceId", conference.getId())
                .param("roomId", room.getId())
                .log().all()
                .when()
                .patch(URI + "/book/" + room.getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
                .log().all();
    }

    @Test
    void testPatchBookRoomShouldReturnBadRequest() throws JSONException {
        Integer count = 10;
        Room room = Room.builder()
                .seatsCount(count)
                .number(count)
                .floor(count)
                .roomAvailability(new ArrayList<>())
                .build();

        roomRepository.save(room);

        Conference conference = Conference.builder()
                .eventDate(new BsonTimestamp(10, 100))
                .status(ConferenceStatus.SCHEDULED.name())
                .requestedSeatsCount(count + 1)
                .build();

        conferenceRepository.save(conference);

        JSONObject roomParams = new JSONObject();
        roomParams.put("seatsCount", count + 1);

        given()
                .contentType(ContentType.JSON)
                .header("conferenceId", conference.getId())
                .param("roomId", room.getId())
                .log().all()
                .when()
                .patch(URI + "/book/" + room.getId())
                .then()
                .assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .log().all();
    }
}
