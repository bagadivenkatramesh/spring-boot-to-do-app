package com.app.todoapp.controllers;

import com.app.todoapp.models.Task;
import com.app.todoapp.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private List<Task> mockTasks;

    @BeforeEach
    public void setUp() {
        // Create mock tasks
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTask("Buy groceries");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTask("Complete project");
        task2.setCompleted(true);

        mockTasks = Arrays.asList(task1, task2);
    }

    @Test
    public void testGetTasks_ReturnsCorrectViewNameAndPopulatesModel() throws Exception {
        // Arrange
        when(taskService.getAllTasks()).thenReturn(mockTasks);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("tasks", hasSize(2)))
                .andExpect(model().attribute("tasks", hasItems(
                        hasProperty("id", is(1L)),
                        hasProperty("id", is(2L))
                )));

        // Verify the service was called once
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    public void testGetTasks_ModelContainsTaskDetails() throws Exception {
        // Arrange
        when(taskService.getAllTasks()).thenReturn(mockTasks);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tasks", hasItems(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("task", is("Buy groceries")),
                                hasProperty("completed", is(false))
                        ),
                        allOf(
                                hasProperty("id", is(2L)),
                                hasProperty("task", is("Complete project")),
                                hasProperty("completed", is(true))
                        )
                )));
    }

    @Test
    public void testCreateTask_RedirectsAndCallsService() throws Exception {
        // Arrange
        String taskTitle = "New Task";
        doNothing().when(taskService).createTask(taskTitle);

        // Act & Assert
        mockMvc.perform(post("/")
                        .param("title", taskTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify the service was called with the correct parameter
        verify(taskService, times(1)).createTask(taskTitle);
    }

    @Test
    public void testCreateTask_CallsServiceWithCorrectTitle() throws Exception {
        // Arrange
        String taskTitle = "Buy milk";
        doNothing().when(taskService).createTask(taskTitle);

        // Act & Assert
        mockMvc.perform(post("/")
                        .param("title", taskTitle))
                .andExpect(status().is3xxRedirection());

        // Verify service interaction
        verify(taskService).createTask(eq(taskTitle));
        verifyNoMoreInteractions(taskService);
    }

    @Test
    public void testDeleteTask_RedirectsAndCallsService() throws Exception {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(get("/delete/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify the service was called with the correct ID
        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    public void testUpdateTask_RedirectsAndCallsService() throws Exception {
        // Arrange
        Long taskId = 1L;
        String taskTitle = "Updated task";
        doNothing().when(taskService).updateTask(taskId, taskTitle);

        // Act & Assert
        mockMvc.perform(get("/update/{id}", taskId)
                        .param("taskTitle", taskTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify the service was called with the correct parameters
        verify(taskService, times(1)).updateTask(taskId, taskTitle);
    }

    @Test
    public void testToggleTask_RedirectsAndCallsService() throws Exception {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).toggleTask(taskId);

        // Act & Assert
        mockMvc.perform(get("/toggle/{id}", taskId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Verify the service was called with the correct ID
        verify(taskService, times(1)).toggleTask(taskId);
    }

    @Test
    public void testGetTasks_EmptyTaskList() throws Exception {
        // Arrange
        when(taskService.getAllTasks()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(model().attribute("tasks", hasSize(0)));

        verify(taskService, times(1)).getAllTasks();
    }
}
