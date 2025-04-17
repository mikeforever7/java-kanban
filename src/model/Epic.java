package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasksInEpic;

    public Epic (int code, String name, String description) {
        super(code, name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }
 }
