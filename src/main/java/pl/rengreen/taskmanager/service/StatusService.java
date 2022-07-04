package pl.rengreen.taskmanager.service;

import pl.rengreen.taskmanager.model.ProjectStatus;

import java.util.List;

public interface StatusService {
    ProjectStatus createStatus(ProjectStatus status);
    ProjectStatus findByStatusName(String name);

    List<ProjectStatus> findAll();
}
