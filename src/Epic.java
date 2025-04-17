import java.util.ArrayList;

public class Epic extends Task {

    public ArrayList<Subtask> subtasksInEpic;

    public Epic (int code, String name, String description) {
        super(code, name, description);
        subtasksInEpic = new ArrayList<>();
    }
}
