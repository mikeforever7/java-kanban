package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, startTime, duration);
        this.type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.type = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration, int epicID) {
        super(id, name, description, status, startTime, duration);
        this.type = TaskType.SUBTASK;
        this.epicId = epicID;
    }

    public Subtask(int id, String name, String description, LocalDateTime startTime, Duration duration, int epicID) {
        super(id, name, description, startTime, duration);
        this.type = TaskType.SUBTASK;
        this.epicId = epicID;
    }

    public int getEpicId() {
        return epicId;
    }
}
