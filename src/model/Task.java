package model;

public class Task {
    private String name;
    private String description;
    private int code;
    private TaskStatus status;


    public Task (int code, String name, String description) {
        this.name = name;
        this.description = description;
        this.code = code;
        status = TaskStatus.NEW;
    }

    public Task (int code, String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.status = status;
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code=" + code +
                ", status=" + status +
                '}';
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
