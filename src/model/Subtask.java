package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask (int code, String name, String description, int epicId) {
        super(code, name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
