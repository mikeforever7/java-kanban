package manager;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            String stringFromFile = Files.readString(file.toPath());
            String[] stringsFromFile = stringFromFile.split("\n");
            for (int i = 1; i < stringsFromFile.length; i++) {
                Task task = taskManager.fromString(stringsFromFile[i]);
                //  Здесь меняю счетчик на верный.
                if (task.getId() > taskManager.counter) {
                    taskManager.counter = task.getId();
                }
                // Здесь проверяю какого типа задача и кладу в нужную мапу (без использования счетчика)
                if (task instanceof Epic epic) {
                    taskManager.addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    taskManager.addSubtask(subtask);
                } else {
                    taskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Файл отсутствует");
        }
        return taskManager;
    }

    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Integer key : tasks.keySet()) {
                Task task = tasks.get(key);
                fileWriter.write(toString(task) + "\n");
            }
            for (Integer key : epics.keySet()) {
                Epic epic = epics.get(key);
                fileWriter.write(toString(epic) + "\n");
            }
            for (Integer key : subtasks.keySet()) {
                Subtask subtask = subtasks.get(key);
                fileWriter.write(toString(subtask) + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public Task fromString(String value) {
        String[] field = value.split(",");
        String type = field[1];
        switch (type) {
            case "TASK" -> {
                int id = Integer.parseInt(field[0]);
                TaskStatus status = TaskStatus.valueOf(field[3]);
                return new Task(id, field[2], field[4], status);
            }
            case "EPIC" -> {
                int id = Integer.parseInt(field[0]);
                TaskStatus status = TaskStatus.valueOf(field[3]);
                return new Epic(id, field[2], field[4], status);
            }
            case "SUBTASK" -> {
                int id = Integer.parseInt(field[0]);
                TaskStatus status = TaskStatus.valueOf(field[3]);
                int epicId = Integer.parseInt(field[5]);
                return new Subtask(id, field[2], field[4], status, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    public String toString(Task task) {
        if (task.getType() == null) {
            throw new IllegalArgumentException("Тип задачи не может быть null.");
        }
        return switch (task.getType()) {
            case TASK, EPIC -> task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                    "," + task.getDescription();
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                yield task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                        "," + task.getDescription() + "," + subtask.getEpicId();
            }
        };
    }

    @Override
    public void updateTask(int id, String newName, String newDescription, TaskStatus newStatus) {
        super.updateTask(id, newName, newDescription, newStatus);
        save();
    }

    @Override
    public void updateEpic(int id, String newName, String newDescription) {
        super.updateEpic(id, newName, newDescription);
        save();
    }

    @Override
    public void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus) {
        super.updateSubtask(id, newName, newDescription, newStatus);
        save();
    }

    @Override
    public void addTask(String name, String description) {
        super.addTask(name, description);
        save();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(String name, String description) {
        super.addEpic(name, description);
        save();
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(String name, String description, int epicId) {
        super.addSubtask(name, description, epicId);
        save();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    // Дополнительное задание
    public static void main(String[] args) {
        Path path = Paths.get("c:\\Users\\user\\IdeaProjects\\java-kanban\\taskManager.csv");
        FileBackedTaskManager fileBackedTaskManagerFirst = new FileBackedTaskManager(path.toFile());
        fileBackedTaskManagerFirst.addTask("Задача один", "Создана");
        fileBackedTaskManagerFirst.addEpic("Первый эпик", "С ID 2");
        fileBackedTaskManagerFirst.addSubtask("Подзадача", "Для эпика", 2);
        FileBackedTaskManager fileBackedTaskManagerSecond = FileBackedTaskManager.loadFromFile(path.toFile());
        if (fileBackedTaskManagerFirst.tasks.get(1).equals(fileBackedTaskManagerSecond.tasks.get(1)) &&
                fileBackedTaskManagerFirst.epics.get(2).equals(fileBackedTaskManagerSecond.epics.get(2)) &&
                fileBackedTaskManagerFirst.subtasks.get(3).equals(fileBackedTaskManagerSecond.subtasks.get(3))) {
            System.out.println("Все задачи из старого и нового менеджеров совпадают");
        }
        System.out.println(fileBackedTaskManagerFirst.tasks);
        System.out.println(fileBackedTaskManagerSecond.tasks);
        System.out.println(fileBackedTaskManagerFirst.epics);
        System.out.println(fileBackedTaskManagerSecond.epics);
        System.out.println(fileBackedTaskManagerSecond.subtasks);
        System.out.println(fileBackedTaskManagerSecond.subtasks);
    }
}
