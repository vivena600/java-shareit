package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers()
                .stream().map(UserMapper::mapUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.mapUserDto(repository.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto user) {
        User userEntity = UserMapper.mapUser(user);
        return UserMapper.mapUserDto(repository.createUser(userEntity));
    }

    @Override
    public UserDto updateUser(UserDto user) {
        User userEntity = UserMapper.mapUser(user);
        return UserMapper.mapUserDto(repository.updateUser(userEntity));
    }

    @Override
    public void deleteUser(Long id) {
        repository.deleteUser(id);
    }
}
