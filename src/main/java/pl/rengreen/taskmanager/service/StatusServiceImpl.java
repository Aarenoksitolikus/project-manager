package pl.rengreen.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.rengreen.taskmanager.model.ProjectStatus;
import pl.rengreen.taskmanager.repository.StatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService {

    private StatusRepository statusRepository;

    @Autowired
    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public ProjectStatus createStatus(ProjectStatus status) {
        return statusRepository.save(status);
    }

    @Override
    public ProjectStatus findByStatusName(String name) {
        Optional<ProjectStatus> byName = statusRepository.findByName(name);
        if (byName.isPresent()) {
            return byName.get();
        } else {
            throw new RuntimeException("Could not find a project status with name: " + name);
        }
    }

    @Override
    public List<ProjectStatus> findAll() {
        return statusRepository.findAll();
    }
}
