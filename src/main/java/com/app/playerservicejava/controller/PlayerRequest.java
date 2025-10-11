package com.app.playerservicejava.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlayerRequest {

    @NotBlank
    private String birthYear;

    @NotBlank
    private String birthMonth;

    @NotBlank
    private String birthDay;

    @NotBlank
    private String birthCountry;

    @NotBlank
    private String birthState;

    @NotBlank
    private String birthCity;

    private String deathYear;

    private String deathMonth;

    private String deathDay;

    private String deathCountry;

    private String deathState;

    private String deathCity;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String givenName;

    private String weight;

    private String height;

    private String bats;

    private String throwStats;

    private String debut;

    private String finalGame;

    private String retroId;

    private String bbrefId;

}
