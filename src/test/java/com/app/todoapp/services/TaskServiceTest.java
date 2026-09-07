package com.app.todoapp.services;

import com.app.todoapp.models.Task;
import com.app.todoapp.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_returnsListFromRepository_andCallsFindAll() {
        // Arrange
        Task t1 = new Task();
        t1.setTask("Task 1");
        t1.setCompleted(false);

        Task t2 = new Task();
        t2.setTask("Task 2");
        t2.setCompleted(true);

        List<Task> tasks = Arrays.asList(t1, t2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tasks, result);
        verify(taskRepository, times(1)).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void createTask_savesNewTask_withGivenTitleAndNotCompleted() {
        // Arrange
        String title = "New task title";
        // Make save() return the saved Task (echo back) if code depends on its return.
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        taskService.createTask(title);

        // Assert - capture argument passed to save
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(captor.capture());
        Task saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(title, saved.getTask());
        assertFalse(saved.isCompleted());
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void toggleTask_flipsCompletedAndSaves() {
        // Arrange
        Long id = 1L;
        Task existing = new Task();
        existing.setTask("Some task");
        existing.setCompleted(false);

        when(taskRepository.findById(eq(id))).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        taskService.toggleTask(id);

        // Assert
        verify(taskRepository, times(1)).findById(eq(id));
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(captor.capture());
        Task saved = captor.getValue();

        assertNotNull(saved);
        assertTrue(saved.isCompleted(), "Task completed flag should be toggled to true");
        verifyNoMoreInteractions(taskRepository);
    }
}