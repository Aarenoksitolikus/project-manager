package pl.rengreen.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rengreen.taskmanager.model.Project;
import pl.rengreen.taskmanager.model.ProjectStatus;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.service.ProjectService;
import pl.rengreen.taskmanager.service.StatusService;
import pl.rengreen.taskmanager.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private UserService userService;

    private ProjectService projectService;

    private StatusService statusService;

    @Autowired
    public ProjectController(UserService userService, ProjectService projectService, StatusService statusService) {
        this.userService = userService;
        this.projectService = projectService;
        this.statusService = statusService;
    }

    @GetMapping("/projects")
    public String listProjects(Model model, Principal principal, SecurityContextHolderAwareRequestWrapper request) {
        prepareProjectsListModel(model, principal, request);
        Project project = Project.builder()
                .creatorName(principal.getName())
                .build();
        model.addAttribute("project", project);
        model.addAttribute("onlyInProgress", false);
        return "views/projects";
    }

    @GetMapping("/projects/in-progress")
    public String listTasksInProgress(Model model, Principal principal, SecurityContextHolderAwareRequestWrapper request) {
        prepareProjectsListModel(model, principal, request);
        Project project = Project.builder()
                .creatorName(principal.getName())
                .build();
        model.addAttribute("project", project);
        model.addAttribute("onlyInProgress", true);
        return "views/projects";
    }

    private void prepareProjectsListModel(Model model, Principal principal, SecurityContextHolderAwareRequestWrapper request) {
        String email = principal.getName();
        User signedUser = userService.getUserByEmail(email);
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN");

        List<Project> currentProjects;

        if (isAdminSigned) {
            currentProjects = projectService.getAllProjects();
        } else {
            currentProjects = projectService.getAllProjects().stream()
                    .filter(p -> p.getParticipants()
                            .contains(signedUser))
                    .collect(Collectors.toList());
        }

        System.err.println();
        for (Project current : currentProjects) {
            System.err.println(current.getName());
        }
        System.err.println();

        model.addAttribute("projects", currentProjects);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("signedUser", signedUser);
        model.addAttribute("isAdminSigned", isAdminSigned);

    }

    @GetMapping("/project/create")
    public String showEmptyProjectForm(Model model, Principal principal, SecurityContextHolderAwareRequestWrapper request) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        Project project = new Project();
        project.setCreatorName(user.getName());
        List<User> users = userService.findAll();
        model.addAttribute("ownerCandidates", users);
        if (request.isUserInRole("ROLE_USER")) {
            throw new RuntimeException("Project couldn't be created by User. Please, contact Admin");
        }
        model.addAttribute("project", project);
        return "forms/project-new";
    }

    @PostMapping("/project/create")
    public String createProject(@Valid Project project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "forms/project-new";
        }

        ProjectStatus status = statusService.findByStatusName("In Progress");
        project.setStatus(status);
        projectService.createProject(project);

        System.err.println();
        System.err.println(project.getName() + " : " + project.getOwner().getName());
        for (User current : project.getParticipants()) {
            System.err.println(current.getName());
        }
        System.err.println();

        return "redirect:/projects";
    }

    @GetMapping("/project/edit/{id}")
    public String showFilledProjectForm(@PathVariable Integer id, Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("ownerCandidates", users);
        model.addAttribute("project", projectService.getProjectById(id));
        return "forms/project-edit";
    }

    @PostMapping("/project/edit/{id}")
    public String updateProject(@Valid Project project, BindingResult bindingResult,
                                @PathVariable Integer id, Model model) {
        if (bindingResult.hasErrors()) {
            return "forms/project-edit";
        }
        projectService.updateProjectById(id, project);
        return "redirect:/projects";
    }

    @GetMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
        return "redirect:/projects";
    }

    @GetMapping("/project/mark-done/{id}")
    public String setTaskCompleted(@PathVariable Integer id) {
        projectService.setProjectStatusDone(id);
        return "redirect:/projects";
    }

    @GetMapping("/project/unmark-done/{id}")
    public String setTaskNotCompleted(@PathVariable Integer id) {
        projectService.setProjectStatusInProgress(id);
        return "redirect:/projects";
    }
}
