package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PersonDto(
        @JsonProperty("name") String name,
        @JsonProperty("children") List<PersonDto> children) {}
