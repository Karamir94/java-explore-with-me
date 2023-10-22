package ru.practicum.stats.server.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.server.hit.model.Hit;

public interface StatsRepository extends JpaRepository<Hit, Long>, HitRepository {
}