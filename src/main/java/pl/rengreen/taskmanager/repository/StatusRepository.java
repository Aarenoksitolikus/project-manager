package pl.rengreen.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rengreen.taskmanager.model.ProjectStatus;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<ProjectStatus, Integer> {
    Optional<ProjectStatus> findByName(String statusName);
}
