package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream().map(UserMapper::mapUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user =  repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не удалось нйти пользователя с id:" + id));
        return UserMapper.mapUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto user) {
        User userEntity = UserMapper.mapUser(user);
        return UserMapper.mapUserDto(repository.save(userEntity));
    }

    @Override
    public UserDto updateUser(UserDto user) {
        User oldUser = repository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Не удалось нйти пользователя с id:" + user.getId()));

        if (user.getEmail() != null && !oldUser.getEmail().equals(user.getEmail())) {
            oldUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !oldUser.getName().equals(user.getName())) {
            oldUser.setName(user.getName());
        }

        return UserMapper.mapUserDto(repository.save(oldUser));
    }

    @Override
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не удалось нйти пользователя с id:" + id));
        repository.delete(user);
    }
}
