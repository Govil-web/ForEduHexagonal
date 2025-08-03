package com.academia.domain.model.entities;

import com.academia.domain.model.enums.RoleScope;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Role {
    private final Integer id;
    private final String name;
    private final RoleScope scope;
    private final Set<String> permissions; // Ej: "student:create", "course:assign_teacher"

    public Role(Integer id, String name, RoleScope scope, Set<String> permissions) {
        this.id = id;
        this.name = name;
        this.scope = scope;
        this.permissions = new HashSet<>(permissions);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}