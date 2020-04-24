package ru.malltshik.trobot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.malltshik.trobot.entities.TraderConfig;

public interface TraderConfigRepository extends JpaRepository<TraderConfig, Long> {
}
