package pl.rengreen.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.rengreen.taskmanager.model.User;
import pl.rengreen.taskmanager.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String listUsers(Model model, SecurityContextHolderAwareRequestWrapper request, Principal principal) {
        String email = principal.getName();
        User signedUser = userService.getUserByEmail(email);
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN");

        List<User> users;

        if (isAdminSigned) {
            users = userService.findAll();
        } else {
            users = userService.getAllByMyProject(signedUser);
        }

        model.addAttribute("users", users);
        model.addAttribute("isAdminSigned", isAdminSigned);
        return "views/users";
    }

    @GetMapping("user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

}
