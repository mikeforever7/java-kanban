package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected int counter = 0;

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager = new InMemoryHistoryManager();

    //Получить историю
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Проверка выполнения подзадач
    @Override
    public TaskStatus checkEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        for (Subtask subtask : epic.getSubtasksInEpic()) {
            if (!subtask.getStatus().equals(TaskStatus.NEW)) {
                for (Subtask otherSubtask : epic.getSubtasksInEpic()) {
                    if (!otherSubtask.getStatus().equals(TaskStatus.DONE)) {
                        return TaskStatus.IN_PROGRESS;
                    }
                }
                return TaskStatus.DONE;
            }
        }
        return TaskStatus.NEW;
    }

    //Обновление задач
    @Override
    public void updateTask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Task task = getTask(id);
        task.setName(newName);
        task.setDescription(newDescription);
        task.setStatus(newStatus);
    }

    @Override
    public void updateEpic(int id, String newName, String newDescription) {
        Epic epic = getEpic(id);
        epic.setName(newName);
        epic.setDescription(newDescription);
    }

    @Override
    public void updateSubtask(int id, String newName, String newDescription, TaskStatus newStatus) {
        Subtask subtask = getSubtask(id);
        subtask.setName(newName);
        subtask.setDescription(newDescription);
        subtask.setStatus(newStatus);
        Epic epic = getEpic(subtask.getEpicId());
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Получить по id
    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    //Добавление задач
    @Override
    public void addTask(String name, String description) {
        getID();
        Task task = new Task(counter, name, description);
        tasks.put(counter, task);
    }

    @Override
    public void addEpic(String name, String description) {
        getID();
        Epic epic = new Epic(counter, name, description);
        epics.put(counter, epic);
    }

    @Override
    public void addSubtask(String name, String description, int epicId) {
        getID();
        Subtask subtask = new Subtask(counter, name, description, epicId);
        subtasks.put(counter, subtask);
        Epic epic = getEpic(epicId);
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
    }

    //Очистка списков
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            List<Subtask> subtasksCopy = new ArrayList<>(epic.getSubtasksInEpic());
            for (Subtask subtask : subtasksCopy) {
                deleteSubtaskById(subtask.getId());
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            epic.getSubtasksInEpic().clear();
            epic.setStatus(checkEpicStatus(key));
        }
    }

    private void getID() {
        counter++;
    }

    //Удаление по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        List<Subtask> subtasksCopy = new ArrayList<>(epic.getSubtasksInEpic());
        for (Subtask subtask : subtasksCopy) {
            deleteSubtaskById(subtask.getId());
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        int epicId = subtask.getEpicId();
        subtasks.remove(id);
        epic.getSubtasksInEpic().remove(subtask);
        epic.setStatus(checkEpicStatus(epicId));
        historyManager.remove(id);
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }
}

