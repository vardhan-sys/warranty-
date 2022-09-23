package com.geaviation.techpubs.models.techlib.dto;

import java.util.UUID;

public class AirframeDto {

    private UUID id;
    private String airframe;

    public AirframeDto() {}

    public AirframeDto(UUID id, String airframe) {
        this.id = id;
        this.airframe = airframe;
    }

    public UUID getId() {return id;}

    public void setId(UUID id) {this.id = id;}

    public String getAirframe() {return airframe;}

    public void setAirframe(String airframe) {this.airframe = airframe;}
}
