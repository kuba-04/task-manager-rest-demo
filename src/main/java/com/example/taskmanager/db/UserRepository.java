package com.example.taskmanager.db;

import com.example.taskmanager.domain.User;
import com.example.taskmanager.domain.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UserId>, CrudRepository<User, UserId> {

    @Query("SELECT u FROM User u WHERE " +
            "(:id IS NULL OR u.id = :id) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<User> findBySearchParams(@Param("id") UserId id,
                                  @Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("email") String email,
                                  Pageable pageable);

}
