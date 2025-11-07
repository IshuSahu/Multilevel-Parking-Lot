package com.example.multilevel_parking_lot.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_lot")
@Getter
@Setter
@NoArgsConstructor
public class ParkingLot {
    @Id
    private String id; // allow human ids like "lot-1"

    private String name;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Level> levels = new ArrayList<>();
}
