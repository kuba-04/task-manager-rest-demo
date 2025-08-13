package com.example.taskmanager.db;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.domain.TaskId;
import com.example.taskmanager.domain.TaskStatus;
import com.example.taskmanager.domain.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, TaskId>, CrudRepository<Task, TaskId> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:description IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
            "(:taskStatus IS NULL OR t.taskStatus = :taskStatus) AND " +
            "(:deadlineFrom IS NULL OR t.deadline >= :deadlineFrom) AND " +
            "(:deadlineTo IS NULL OR t.deadline <= :deadlineTo) AND " +
            "(:assignedUserId IS NULL OR :assignedUserId MEMBER OF t.assignedUsers)")
    Page<Task> findBySearchParams(@Param("title") String title,
                                  @Param("description") String description,
                                  @Param("taskStatus") TaskStatus taskStatus,
                                  @Param("deadlineFrom") LocalDateTime deadlineFrom,
                                  @Param("deadlineTo") LocalDateTime deadlineTo,
                                  @Param("assignedUserId") UserId assignedUserId,
                                  Pageable pageable);
}
