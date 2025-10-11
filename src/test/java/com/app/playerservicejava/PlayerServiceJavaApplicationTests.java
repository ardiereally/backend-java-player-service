package com.app.playerservicejava;

import com.app.playerservicejava.model.Players;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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


}
