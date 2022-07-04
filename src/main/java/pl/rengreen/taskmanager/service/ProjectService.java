package pl.rengreen.taskmanager.service;

import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.User;

import java.util.List;

public interface ProjectService {
    Boolean isProjectIdPresent(Integer id);
    Project getProjectById(Integer id);
    List<Project> findByName(String title);
    Project createProject(Project project);
    Project saveProject(Project project);
    List<Project> getAllProjects();
    Project updateProjectById(Integer id, Project projectToBeUpdated);
    void deleteProjectById(Integer id);

    void setProjectStatusDone(Integer id);

    void setProjectStatusInProgress(Integer id);

    void assignUserToProject(Project project, User user);
}
