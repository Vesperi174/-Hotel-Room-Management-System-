package com.hotel.model.entity;

public class Role {
    private Integer roleId;
    private String roleName;
    private String permissions;
    private String description;

    public Role() {}

    public Role(Integer roleId, String roleName, String permissions) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean hasPermission(String permission) {
        return permissions != null && (permissions.equals("ALL") || permissions.contains(permission));
    }

    @Override
    public String toString() {
        return "Role{roleId=" + roleId + ", roleName='" + roleName + "'}";
    }
}