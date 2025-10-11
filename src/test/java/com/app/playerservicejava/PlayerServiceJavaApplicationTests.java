package com.app.playerservicejava;

import com.app.playerservicejava.controller.PlayerRequest;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerServiceJavaApplicationTests {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void contextLoads() {
    }

    @Test
    public void testPagedRequest() {
        ResponseEntity<Players> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}", Players.class, Map.of("page", 0, "size", 50)
        );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(50, response.getBody().getPlayers().size());
    }

    @Test
    public void testPagedRequestSortedByBirthyear() {
        ResponseEntity<Players> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}&sortBy=birthYear&orderBy=asc", Players.class, Map.of("page", 0, "size", 50)
        );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(50, response.getBody().getPlayers().size());
    }

    @Test
    public void testPagedRequestBadSize() {
        ResponseEntity<String> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}", String.class, Map.of("page", 0, "size", 12000)
        );
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void testPagedRequestBadPage() {
        ResponseEntity<String> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}", String.class, Map.of("page", -40, "size", 100)
        );
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void testPagedRequestBadOrder() {
        ResponseEntity<String> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}&orderBy=random", String.class, Map.of("page", -40, "size", 100)
        );
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void testPagedRequestInvalidPage() {
        ResponseEntity<Players> response = rest.getForEntity(
                "/v1/players?page={page}&size={size}", Players.class, Map.of("page", 200, "size", 100)
        );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().getPlayers().isEmpty());
    }

    @Test
    public void addingNewPlayer() {
        TestRestTemplate restWithAuth = rest.withBasicAuth("admin", "pass123");

        // save a new player
        PlayerRequest request = new PlayerRequest();
        request.setBirthCity("CCU");
        request.setBirthDay("12");
        request.setBirthCountry("Mexico");
        request.setBirthMonth("March");
        request.setBirthState("Chihuahua");
        request.setBirthYear("1991");
        request.setFirstName("Juan");
        request.setLastName("Garcia");
        request.setWeight("119.5");
        request.setHeight("179");
        // get id
        ResponseEntity<Player> addedPlayer = restWithAuth.postForEntity("/v1/players", request, Player.class);
        assertEquals(HttpStatus.CREATED, addedPlayer.getStatusCode());
        assertNotNull(addedPlayer.getBody());
        String newPlayerId = addedPlayer.getBody().getPlayerId();
        // fetch new player by id
        ResponseEntity<Player> playerAdded = rest.getForEntity("/v1/players/{id}", Player.class, Map.of("id", newPlayerId));
        assertEquals(HttpStatus.OK, playerAdded.getStatusCode());
        assertNotNull(playerAdded.getBody());
        // ensure fields correctly saved
        assertEquals(playerAdded.getBody().getBirthCity(), request.getBirthCity());
        assertEquals(playerAdded.getBody().getBirthCountry(), request.getBirthCountry());
        assertEquals(playerAdded.getBody().getBirthDay(), request.getBirthDay());
        assertEquals(playerAdded.getBody().getBirthMonth(), request.getBirthMonth());
        assertEquals(playerAdded.getBody().getBirthState(), request.getBirthState());
        assertEquals(playerAdded.getBody().getBirthYear(), request.getBirthYear());
        assertEquals(playerAdded.getBody().getFirstName(), request.getFirstName());
        assertEquals(playerAdded.getBody().getLastName(), request.getLastName());
        assertEquals(playerAdded.getBody().getWeight(), request.getWeight());
        assertEquals(playerAdded.getBody().getHeight(), request.getHeight());
    }

    @Test
    public void addingInvalidPlayer() {
        TestRestTemplate restWithAuth = rest.withBasicAuth("admin", "pass123");
        // save a new player
        PlayerRequest request = new PlayerRequest();
        request.setBirthCity(null); // invalid
        request.setBirthDay("12");
        request.setBirthCountry("Mexico");
        request.setBirthMonth("March");
        request.setBirthState("Chihuahua");
        request.setBirthYear("1991");
        request.setFirstName("Juan");
        request.setLastName("Garcia");
        request.setWeight("119.5");
        request.setHeight("179");
        // get id
        ResponseEntity<Player> addedPlayer = restWithAuth.postForEntity("/v1/players", request, Player.class);
        assertTrue(addedPlayer.getStatusCode().is4xxClientError());
    }


}
