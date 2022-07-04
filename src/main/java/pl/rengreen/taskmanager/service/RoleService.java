package pl.rengreen.taskmanager.service;

import pl.rengreen.taskmanager.model.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);

    List<Role> findAll();
}
