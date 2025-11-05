package com.example.multilevel_parking_lot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateLotRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    private List<LevelDto> levels;

    @Data
    public static class LevelDto {
        @Min(1)
        private int levelNumber;

        @Min(0)
        private int spots;
    }
}
