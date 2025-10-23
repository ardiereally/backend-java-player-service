package com.app.playerservicejava.controller;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "v1/players", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class PlayerController {

    private static final int MAX_PAGE_SIZE = 1000;

    @Resource
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<Players> getPlayers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(0) @Max(MAX_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "playerId") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|dsc") String order) {

        Players players = playerService.getPlayerPage(page, size, sortBy, order);
        return ok(players);
    }

    @PostMapping
    public ResponseEntity<Player> addPlayer(@Valid @RequestBody PlayerRequest playerRequest) {
        Player player = mapRequestToModel(playerRequest);
        Player savedPlayer = playerService.addPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") String id,
                                               @Valid @RequestBody PlayerRequest playerRequest) {
        Player player = mapRequestToModel(playerRequest);
        Player savedPlayer = playerService.updatePlayer(id, player);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
    }

    private Player mapRequestToModel(@Valid PlayerRequest playerRequest) {
        Player player = new Player();
        player.setBirthCity(playerRequest.getBirthCity());
        player.setBats(playerRequest.getBats());
        player.setBbrefId(playerRequest.getBbrefId());
        player.setBirthCountry(playerRequest.getBirthCountry());
        player.setBirthDay(playerRequest.getBirthDay());
        player.setBirthMonth(playerRequest.getBirthMonth());
        player.setBirthState(playerRequest.getBirthState());
        player.setBirthYear(playerRequest.getBirthYear());
        player.setDeathCity(playerRequest.getDeathCity());
        player.setDeathDay(playerRequest.getDeathDay());
        player.setDeathCountry(playerRequest.getDeathCountry());
        player.setDeathState(playerRequest.getDeathState());
        player.setDeathMonth(playerRequest.getDeathMonth());
        player.setDeathYear(playerRequest.getDeathYear());
        player.setDebut(playerRequest.getDebut());
        player.setBbrefId(playerRequest.getBbrefId());
        player.setFinalGame(playerRequest.getFinalGame());
        player.setFirstName(playerRequest.getFirstName());
        player.setLastName(playerRequest.getLastName());
        player.setWeight(playerRequest.getWeight());
        player.setHeight(playerRequest.getHeight());
        player.setRetroId(playerRequest.getRetroId());
        player.setThrowStats(playerRequest.getThrowStats());
        return player;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") String id) {
        Optional<Player> player = playerService.getPlayerById(id);

        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
