package com.edu.taskmanager.repository;

import com.edu.taskmanager.model.Task;
import com.edu.taskmanager.model.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> findByStatus(TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        tasks.remove(id);
    }

    public boolean existsById(String id) {
        return tasks.containsKey(id);
    }

    public void saveAll(List<Task> taskList) {
        taskList.forEach(task -> tasks.put(task.getId(), task));
    }
}