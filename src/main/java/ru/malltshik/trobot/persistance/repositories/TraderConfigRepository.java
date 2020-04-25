package ru.malltshik.trobot.persistance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.malltshik.trobot.persistance.entities.TraderConfig;

public interface TraderConfigRepository extends JpaRepository<TraderConfig, Long> {
}
