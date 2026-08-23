package com.app.todoapp.services;

import com.app.todoapp.models.Task;
import com.app.todoapp.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void createTask(String title) {
        Task task = new Task();
        task.setTask(title);
        task.setCompleted(false);
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public void updateTask(Long id, String taskTitle) {
        Task task = taskRepository.findById(id).orElseThrow(()->new IllegalArgumentException("No task with that id found"));
        task.setTask(taskTitle);
        taskRepository.save(task);
    }

    public void toggleTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("no such task with that id found"));
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }
}
