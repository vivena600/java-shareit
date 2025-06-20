package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
}
