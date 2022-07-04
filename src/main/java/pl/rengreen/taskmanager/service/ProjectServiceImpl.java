package pl.rengreen.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.ProjectStatus;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.repository.ProjectRepository;
import pl.rengreen.taskmanager.repository.StatusRepository;
import pl.rengreen.taskmanager.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;

    private StatusRepository statusRepository;

    private UserRepository userRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              StatusRepository statusRepository,
                              UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Boolean isProjectIdPresent(Integer id) {

        return projectRepository.findById(id).isPresent();
    }

    @Transactional
    @Override
    public Project getProjectById(Integer id) {

        Optional<Project> byId = projectRepository.findProjectByProjectId(id);

        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new RuntimeException("Could not find a project with the id: " + id);
            // todo ELSE throw exception
        }
    }

    @Transactional
    @Override
    public List<Project> findByName(String title) {

        return projectRepository.findByName(title);
    }

    @Override
    public Project createProject(Project project) {
        if (project.getParticipants() == null) {
            project.setParticipants(List.of(project.getOwner()));
        } else {
            project.getParticipants().add(project.getOwner());
        }
        return projectRepository.save(project);
    }

    @Transactional
    @Override
    public Project saveProject(Project project) {

        Optional<ProjectStatus> byStatusName = statusRepository.findByName(project.getStatus().getName());

        ProjectStatus status;
        if (!byStatusName.isPresent()) {

            ProjectStatus newStatus = new ProjectStatus(project.getStatus().getName());
            status = statusRepository.save(newStatus);
        } else {

            status = byStatusName.get();
        }

        Project projectToBeSaved = new Project(project.getName(), status);
        projectToBeSaved.setParticipants(List.of(project.getOwner()));
        return projectRepository.save(projectToBeSaved);
    }

    @Transactional
    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    @Override
    public Project updateProjectById(Integer id, Project projectToBeUpdated) {
        Project project = projectRepository.getOne(id);
        project.setDescription(projectToBeUpdated.getDescription());
        project.setName(projectToBeUpdated.getName());
        project.setOwner(projectToBeUpdated.getOwner());
        project.setDate(projectToBeUpdated.getDate());
        if (!project.getParticipants().contains(projectToBeUpdated.getOwner())) {
            project.getParticipants().add(projectToBeUpdated.getOwner());
        }
        return projectRepository.save(project);
    }

    @Transactional
    @Override
    public void deleteProjectById(Integer id) {

        projectRepository.deleteById(id);
    }

    @Override
    public void setProjectStatusDone(Integer id) {
        Project project = projectRepository.getOne(id);
        ProjectStatus status = statusRepository.getOne(2);
        project.setStatus(status);
        projectRepository.save(project);
    }

    @Override
    public void setProjectStatusInProgress(Integer id) {
        Project project = projectRepository.getOne(id);
        ProjectStatus status = statusRepository.getOne(1);
        project.setStatus(status);
        projectRepository.save(project);
    }

    @Override
    public void assignUserToProject(Project project, User user) {
        project.getParticipants().add(user);
        projectRepository.save(project);
    }
}
