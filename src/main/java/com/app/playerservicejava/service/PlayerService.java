package com.app.playerservicejava.service;

import com.app.playerservicejava.exceptions.PlayerNotFoundException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlayerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

    public Players getPlayers() {
        Players players = new Players();
        playerRepository.findAll()
                .forEach(players.getPlayers()::add);
        return players;
    }

    public Player addPlayer(Player player) {
        var savedPlayer = playerRepository.save(player);
        LOGGER.info("Added player to DB: {}", savedPlayer);
        return savedPlayer;
    }

    public Players getPlayerPage(int pageNum, int size, String sortBy, String orderBy) {
        PageRequest request = PageRequest.of(
                pageNum,
                size,
                "asc".equalsIgnoreCase(orderBy) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );
        Players players = new Players();
        playerRepository.findAll(request).forEach(players.getPlayers()::add);
        return players;
    }

    @Cacheable(cacheNames = "players", key = "#playerId")
    public Optional<Player> getPlayerById(String playerId) {
        /* simulated network delay */
        try {
            Optional<Player> player = playerRepository.findById(playerId);
            Thread.sleep((long) (Math.random() * 2000));
            return player;
        } catch (Exception e) {
            LOGGER.error("message=Exception in getPlayerById; exception={}", e.toString());
        }
        return Optional.empty();
    }

    @CacheEvict(cacheNames = "players", key = "#id")
    @Transactional
    public Player updatePlayer(String id, Player player) {
        Optional<Player> playerToUpdate = getPlayerById(id);
        if (playerToUpdate.isEmpty()) {
            throw new PlayerNotFoundException("No player found by ID " + id);
        }
        player.setPlayerId(id);
        return playerRepository.saveAndFlush(player);
    }
}
