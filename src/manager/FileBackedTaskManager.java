package manager;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        //Проверка наличия файла. Если файла нет, создаем новый.
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
        try {
            String stringFromFile = Files.readString(file.toPath());
            String[] stringsFromFile = stringFromFile.split("\n");
            for (int i = 1; i < stringsFromFile.length; i++) {
                try {
                    Task task = taskManager.fromString(stringsFromFile[i]);
                    //  Здесь меняю счетчик на верный.
                    if (task.getId() > taskManager.counter) {
                        taskManager.counter = task.getId();
                    }
                    // Здесь проверяю какого типа задача и кладу в нужную мапу (без использования счетчика)
                    if (task instanceof Epic epic) {
                        taskManager.addEpicFromFile(epic);
                    } else if (task instanceof Subtask subtask) {
                        taskManager.addSubtaskFromFile(subtask);
                    } else {
                        taskManager.addTaskFromFile(task);
                    }
                } catch (ManagerLoadException e) {
                    // Перехватываем исключение из fromString и выводим сообщение
                    System.err.println("Ошибка при загрузке строки: " + stringsFromFile[i]);
                    System.err.println(e.getMessage());
                    throw new ManagerLoadException(e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки файла");
        }
        return taskManager;
    }

    public Task fromString(String value) {
        String[] field = value.split(",");
        if (!field[1].equals("TASK") && !field[1].equals("EPIC") && !field[1].equals("SUBTASK")) {
            throw new ManagerLoadException("Некорректный тип задачи: " + field[1]);
        }
        String type = field[1];
        int id;
        TaskStatus status;
        try {
            id = Integer.parseInt(field[0]);
            status = TaskStatus.valueOf(field[3]);
        } catch (NumberFormatException e) {
            throw new ManagerLoadException("Некорректный ID задачи: " + field[0]);
        } catch (IllegalArgumentException e) {
            throw new ManagerLoadException("Некорректный статус задачи: " + field[3]);
        }
        LocalDateTime startTime = null;
        Duration duration = null;
        if (!field[5].equals("null") || !field[6].equals("null") && !field[6].equals("PT0S")) {
            try {
                startTime = LocalDateTime.parse(field[5]);
                duration = Duration.parse(field[6]);
            } catch (DateTimeParseException e) {
                throw new ManagerLoadException("Некорректное время задачи: " + field[5] + " " + field[6]);
            }
        }
        switch (type) {
            case "TASK" -> {
                return new Task(id, field[2], field[4], status, startTime, duration);
            }
            case "EPIC" -> {
                return new Epic(id, field[2], field[4], status, startTime, duration);
            }
            case "SUBTASK" -> {
                int epicId;
                try {
                    epicId = Integer.parseInt(field[7]);
                } catch (NumberFormatException e) {
                    throw new ManagerLoadException("Отсутствует верный epicId: " + field[7]);
                }
                return new Subtask(id, field[2], field[4], status, startTime, duration, epicId);
            }
            default -> throw new ManagerLoadException("Неизвестный тип задачи: " + type);

        }
    }

    public void save() {
        try {
            if (!Files.exists(file.toPath())) {
                Files.createFile(file.toPath());
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");

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

    public String toString(Task task) {
        if (task.getType() == null) {
            throw new ManagerSaveException("Тип задачи не может быть null.");
        }
        return switch (task.getType()) {
            case TASK, EPIC -> task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                    "," + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration();
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                yield task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                        "," + task.getDescription() + "," + task.getStartTime() + "," +
                        task.getDuration() + "," + subtask.getEpicId();
            }
        };
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    public void addTaskFromFile(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    public void addEpicFromFile(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    public void addSubtaskFromFile(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
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
}
