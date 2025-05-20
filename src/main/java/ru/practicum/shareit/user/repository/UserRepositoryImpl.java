package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::mapUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return Optional.ofNullable(users.get(id))
                .map(UserMapper :: mapUserDto)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с таким id: " + id));
    }

    @Override
    public UserDto createUser(User user) {
        checkEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return UserMapper.mapUserDto(user);
    }

    @Override
    public UserDto updateUser(User user) {
        UserDto oldUser = getUserById(user.getId());

        if (user.getEmail() != null && !oldUser.getEmail().equals(user.getEmail())) {
            checkEmail(user.getEmail());
            oldUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !oldUser.getName().equals(user.getName())) {
            oldUser.setName(user.getName());
        }

        return oldUser;
    }

    @Override
    public void deleteUser(Long id) {
        //TODO - добавить проверку есть ли пользователь с таким id
        users.remove(id);
    }

    private Long getNextId() {
        return 1 + users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
    }

    private void checkEmail(String email) {
        if (!users.values()
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .collect(Collectors.toList()).isEmpty()) {
            throw new ValidationException("не уникальный email");
        }
    }
}
