package com.app.playerservicejava.controller;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolationException;
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

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") String id) {
        Optional<Player> player = playerService.getPlayerById(id);

        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleIllegalArguments(ConstraintViolationException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
    }

}
