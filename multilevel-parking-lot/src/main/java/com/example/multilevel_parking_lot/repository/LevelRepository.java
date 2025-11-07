package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
}
