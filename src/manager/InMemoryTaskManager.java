package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int counter = 0;

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private HistoryManager historyManager = new InMemoryHistoryManager();

    //Получить историю
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Проверка выполнения подзадач
    @Override
    public TaskStatus checkEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProgress = false; //Здесь, мне кажется, нужно цикл for оставить
        for (Subtask subtask : epic.getSubtasksInEpic()) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                hasNew = true;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                hasInProgress = true;
            }
        }
        if (hasInProgress || (hasNew && hasDone)) {
            return TaskStatus.IN_PROGRESS;
        } else if (!hasNew) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.NEW;
        }
    }

    //Рассчет и установка временных показателей для эпиков
    public void setEpicDurationAndTime(int epicId) {
        Epic epic = epics.get(epicId);
        Duration duration = Duration.ofMinutes(0);
        LocalDateTime minStartTime = null;
        LocalDateTime maxEndTime = null; //Здесь, мне кажется, нужно цикл for оставить
        for (Subtask subtask : epic.getSubtasksInEpic()) {
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                duration = duration.plus((subtask.getDuration()));
                if (minStartTime == null || minStartTime.isAfter(subtask.getStartTime())) {
                    minStartTime = subtask.getStartTime();
                }
                if (maxEndTime == null || maxEndTime.isBefore(subtask.getEndTime())) {
                    maxEndTime = subtask.getEndTime();
                }
            }
        }
        epic.setDuration(duration);
        epic.setStartTime(minStartTime);
        epic.setEndTime(maxEndTime);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    //Тест на пересечение задачи по времени со списком приоритетных задач
    public boolean isTimeCrossing(Task task) {
        return getPrioritizedTasks().stream().anyMatch(task::testCrossingTime);
    }

    //Обновление задач
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задачи с таким id не найдено");
            return;
        }
        if (task.getStartTime() != null && task.getDuration() != null) {
            //Временно удаляю задачу из приоритетов, чтобы не сравнивать с ней же
            prioritizedTasks.remove(getTask(task.getId()));
            if (isTimeCrossing(task)) {
                System.out.println("Задачи пересекаются по времени выполнения");
                //Если не пересеклись, возвращаю
                prioritizedTasks.add(tasks.get(task.getId()));
                return;
            } else {
                prioritizedTasks.add(task);
            }
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпика с таким id не найдено");
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадачи с таким id не найдено");
            return;
        }
        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            //Временно удаляю задачу из приоритетов, чтобы не сравнивать с ней же
            prioritizedTasks.remove(getSubtask(subtask.getId()));
            if (isTimeCrossing(subtask)) {
                System.out.println("Задачи пересекаются по времени выполнения");
                //Если не пересеклись, возвращаю
                prioritizedTasks.add(subtasks.get(subtask.getId()));
                return;
            } else {
                prioritizedTasks.add(subtask);
            }
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.getSubtasksInEpic().remove(getSubtask(subtask.getId()));
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
        setEpicDurationAndTime(subtask.getEpicId());
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
    public void addTask(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            if (isTimeCrossing(task)) {
                System.out.println("Задачи пересекаются по времени выполнения");
                return;
            } else {
                prioritizedTasks.add(task);
            }
        }
        getID();
        task.setId(counter);
        tasks.put(counter, task);
    }

    @Override
    public void addEpic(Epic epic) {
        getID();
        epic.setId(counter);
        epics.put(counter, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик с id=" + subtask.getEpicId() + " не найден");
        }
        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            if (isTimeCrossing(subtask)) {
                System.out.println("Задачи пересекаются по времени выполнения");
                return;
            } else {
                prioritizedTasks.add(subtask);
            }
        }
        getID();
        subtask.setId(counter);
        subtasks.put(counter, subtask);
        Epic epic = getEpic(subtask.getEpicId());
        epic.getSubtasksInEpic().add(subtask);
        epic.setStatus(checkEpicStatus(subtask.getEpicId()));
        setEpicDurationAndTime(subtask.getEpicId());
    }

    //Очистка списков
    @Override
    public void deleteAllTasks() {
        tasks.values().stream()
                .forEach(task -> {
                    historyManager.remove(task.getId());
                    prioritizedTasks.remove(task);
                });
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().stream()
                .forEach(epic -> {
                    epic.getSubtasksInEpic().stream()
                            .forEach(subtask -> {
                                deleteSubtaskById(subtask.getId());
                                prioritizedTasks.remove(subtask);
                            });
                    historyManager.remove(epic.getId());
                });
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().stream()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        subtasks.clear();
        epics.keySet().stream()
                .map(id -> epics.get(id))
                .forEach(epic -> {
                    epic.getSubtasksInEpic().clear();
                    epic.setStatus(checkEpicStatus(epic.getId()));
                    setEpicDurationAndTime(epic.getId());
                });
    }

    private void getID() {
        counter++;
    }

    //Удаление по id
    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(getTask(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        List<Subtask> subtasksCopy = new ArrayList<>(epic.getSubtasksInEpic());
        subtasksCopy.stream()
                .forEach(subtask -> {
                    deleteSubtaskById(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = getEpic(epicId);
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        epic.getSubtasksInEpic().remove(subtask);
        epic.setStatus(checkEpicStatus(epicId));
        setEpicDurationAndTime(epic.getId());
        historyManager.remove(id);
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}

