package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r " +
            "JOIN FETCH r.requestor " +
            "WHERE r.requestor.id != :userId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findByNotUserId(@Param("userId") Long userId);
}
