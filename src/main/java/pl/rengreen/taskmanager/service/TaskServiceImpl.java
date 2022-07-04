package pl.rengreen.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.Task;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.repository.ProjectRepository;
import pl.rengreen.taskmanager.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public void createTask(Task task) {
        taskRepository.save(task);
        projectRepository.save(addUserToBoundedProject(task));
    }

    @Override
    public void updateTask(Long id, Task updatedTask) {
        Task task = taskRepository.getOne(id);
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setDate(updatedTask.getDate());
        task.setOwner(updatedTask.getOwner());
        taskRepository.save(task);
        projectRepository.save(addUserToBoundedProject(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findByOwnerOrderByDateDesc(User user) {
        return taskRepository.findByOwnerOrderByDateDesc(user);
    }

    @Override
    public void setTaskCompleted(Long id) {
        Task task = taskRepository.getOne(id);
        task.setCompleted(true);
        taskRepository.save(task);
    }

    @Override
    public void setTaskNotCompleted(Long id) {
        Task task = taskRepository.getOne(id);
        task.setCompleted(false);
        taskRepository.save(task);
    }

    @Override
    public List<Task> findFreeTasks() {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getOwner() == null && !task.isCompleted())
                .collect(Collectors.toList());

    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public void assignTaskToUser(Task task, User user) {
        task.setOwner(user);
        taskRepository.save(task);
    }

    @Override
    public void unassignTask(Task task) {
        task.setOwner(null);
        taskRepository.save(task);
    }

    @Override
    public List<Task> getAllByMyProject(User user) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(p -> p.getParticipants().contains(user))
                .collect(Collectors.toList());
        List<List<Task>> tasks = projects.stream()
                .map(Project::getTasks)
                .collect(Collectors.toList());
        List<Task> result = new ArrayList<>();
        for (List<Task> current : tasks) {
            result.addAll(current);
        }
        return result;
    }

    private Project addUserToBoundedProject(Task task) {
        User user = task.getOwner();
        Project project = task.getProject();
        project.getParticipants().add(user);
        return project;
    }
}
