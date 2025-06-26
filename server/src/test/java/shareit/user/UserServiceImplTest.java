package shareit.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Spy
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl service;

    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("email1@ya.ru")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("email2@ya.ru")
            .build();

    private void checkUserDto(UserDto user, UserDto userDto) {
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void createUser() {
        when(repository.save(any())).thenReturn(user1);
        UserDto dto = mapper.mapUserDto(user1);

        UserDto result = service.createUser(dto);

        verify(repository, times(1)).save(any());
        checkUserDto(dto, result);
    }

    @Nested
    class GetUser {
        @Test
        public void getAllUsers() {
            when(repository.findAll()).thenReturn(List.of(user1, user2));
            List<UserDto> result = service.getAllUsers();

            verify(repository, times(1)).findAll();
            assertEquals(result.size(), 2, "некорректное количество пользователей");
        }

        @Test
        public void getUserById() {
            when(repository.findById(user1.getId())).thenReturn(Optional.of(user1));
            UserDto dto = mapper.mapUserDto(user1);

            UserDto result = service.getUserById(user1.getId());

            verify(repository, times(1)).findById(user1.getId());
            checkUserDto(dto, result);
        }

        @Test
        public void getUserByFailId() {
            Long failId = 99L;
            when(repository.findById(failId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.getUserById(failId));

            assertEquals("Не удалось нйти пользователя с id:99", exception.getMessage());
            verify(repository, times(1)).findById(anyLong());
        }

        @Test
        public void updateUserNoChanges() {
            when(repository.findById(1L)).thenReturn(Optional.of(user1));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            UserDto updatedDto = UserDto.builder()
                    .id(1L)
                    .name(user1.getName())
                    .email(user1.getEmail())
                    .build();

            UserDto result = service.updateUser(updatedDto);

            assertEquals(user1.getName(), result.getName());
            assertEquals(user1.getEmail(), result.getEmail());

            verify(repository).findById(1L);
            verify(repository).save(any());
        }
    }

    @Nested
    class Delete {
        @Test
        public void deleteUser() {
            when(repository.findById(1L)).thenReturn(Optional.of(user1));

            service.deleteUser(1L);

            verify(repository).findById(1L);
            verify(repository).delete(user1);
        }

        @Test
        public void shouldDelete() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.deleteUser(1L));

            assertEquals("Не удалось нйти пользователя с id:1", exception.getMessage());
            verify(repository, never()).deleteById(anyLong());
        }

        @Test
        public void shouldDeleteIfUserIdNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.deleteUser(99L));

            assertEquals("Не удалось нйти пользователя с id:99", exception.getMessage());
            verify(repository, never()).deleteById(anyLong());
        }
    }

    @Nested
    class UpdateUser {
        @Test
        public void updateUser_shouldUpdateSuccessfully() {
            when(repository.findById(1L)).thenReturn(Optional.of(user1));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // возвращаем обновлённого

            UserDto updatedDto = UserDto.builder()
                    .id(1L)
                    .name("newName")
                    .email("newEmail@ya.ru")
                    .build();

            UserDto result = service.updateUser(updatedDto);

            assertEquals(updatedDto.getId(), result.getId());
            assertEquals("newName", result.getName());
            assertEquals("newEmail@ya.ru", result.getEmail());

            verify(repository).findById(1L);
            verify(repository).save(any());
        }

        @Test
        public void updateUser_shouldUpdateOnlyName() {
            when(repository.findById(1L)).thenReturn(Optional.of(user1));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            UserDto updatedDto = UserDto.builder()
                    .id(1L)
                    .name("changedName")
                    .build();

            UserDto result = service.updateUser(updatedDto);

            assertEquals("changedName", result.getName());
            assertEquals(user1.getEmail(), result.getEmail());

            verify(repository).findById(1L);
            verify(repository).save(any());
        }

        @Test
        public void updateUser_shouldUpdateOnlyEmail() {
            when(repository.findById(1L)).thenReturn(Optional.of(user1));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            UserDto updatedDto = UserDto.builder()
                    .id(1L)
                    .email("changed@ya.ru")
                    .build();

            UserDto result = service.updateUser(updatedDto);

            assertEquals(user1.getName(), result.getName());
            assertEquals("changed@ya.ru", result.getEmail());

            verify(repository).findById(1L);
            verify(repository).save(any());
        }

        @Test
        public void updateUser_shouldThrowIfUserNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            UserDto updatedDto = UserDto.builder()
                    .id(99L)
                    .name("noName")
                    .email("noEmail@ya.ru")
                    .build();

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> service.updateUser(updatedDto));

            assertEquals("Не удалось нйти пользователя с id:99", exception.getMessage());
            verify(repository).findById(99L);
            verify(repository, never()).save(any());
        }
    }
}
