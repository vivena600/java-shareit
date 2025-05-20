package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(User user);

    UserDto updateUser(User user);

    void deleteUser(Long id);
}
