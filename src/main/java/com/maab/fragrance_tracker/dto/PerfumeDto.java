package com.maab.fragrance_tracker.dto;

import java.util.List;

public class PerfumeDto {
    public Long id;
    public String name;
    public String brand;
    public String shortDescription;
    public List<String> tags;
    public boolean inCollection;

    public PerfumeDto() {}

    public PerfumeDto(Long id, String name, String brand, String shortDescription, List<String> tags, boolean inCollection) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.shortDescription = shortDescription;
        this.tags = tags;
        this.inCollection = inCollection;
    }
}
