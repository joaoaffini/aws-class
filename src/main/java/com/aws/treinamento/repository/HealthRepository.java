package com.aws.treinamento.repository;

import com.aws.treinamento.model.Health;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {

    @Query("SELECT h FROM Health h ORDER BY h.timestamp DESC LIMIT 1")
    Optional<Health> findLatest();

}
