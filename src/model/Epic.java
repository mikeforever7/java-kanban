package model;

import java.util.ArrayList;
import java.util.Collection;

public class Epic extends Task {

    private Collection<Subtask> subtasksInEpic;

    public Epic (int code, String name, String description) {
        super(code, name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public Epic (int code, String name, String description, TaskStatus status) {
        super(code, name, description, status);
        subtasksInEpic = new ArrayList<>();
    }

    public Collection<Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }
 }
