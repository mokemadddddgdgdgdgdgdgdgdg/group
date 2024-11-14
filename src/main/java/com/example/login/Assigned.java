package com.example.login;

public class Assigned {
    private String lecturer;
    private String module;
    private String role;
    private String className;

    public Assigned(String lecturer, String module, String role, String className) {
        this.lecturer = lecturer;
        this.module = module;
        this.role = role;
        this.className = className;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
