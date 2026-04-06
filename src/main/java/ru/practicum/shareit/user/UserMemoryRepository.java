package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.tools.IdGenerator;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserMemoryRepository extends IdGenerator {
    private final UserMapper userMapper;
    private final Map<Integer, User> usersMap = new HashMap<>();

    public User addUser(UserDto userDtoRequest) {
        if (userDtoRequest.getEmail() == null || userDtoRequest.getEmail().isEmpty()) {
            log.error("У пользователя должен быть email.");
            throw new ValidationException("У пользователя должен быть email.");
        }

        emailValidator(userDtoRequest.getEmail());

        User user = userMapper.mapToUser(userDtoRequest);

        user.setId(getNextId(usersMap));

        usersMap.put(user.getId(), user);
        log.info("Пользователь с id " + user.getId() + " добавлен");
        return user;
    }

    public List<User> getUsers() {
        List<User> userDtoList = new ArrayList<>(usersMap.values());
        log.info("Все пользователи получены.");
        return userDtoList;
    }

    public User getUserById(Integer userId) {
        return usersMap.get(userId);
    }

    public User updateUser(UserDto userDtoRequest, Integer userId) {
        if (!usersMap.containsKey(userId)) {
            log.error("пользователь с id {} не найден.", userId);
            throw new RuntimeException("Пользователь с id " + userId + " не найден.");
        }

        String email = userDtoRequest.getEmail();

        if (email != null) {
            emailValidator(userDtoRequest.getEmail());
        }

        User user = usersMap.get(userId);

        if (userDtoRequest.hasName()) {
            user.setName(userDtoRequest.getName());
        }

        if (userDtoRequest.hasEmail()) {
            user.setEmail(userDtoRequest.getEmail());
        }

        usersMap.put(userId, user);
        log.info("Пользователь с id {} обновлен", userId);

        return user;
    }

    public User deleteUser(Integer userId) {
        if (usersMap.containsKey(userId)) {
            User user = usersMap.get(userId);
            usersMap.remove(userId);

            log.info("Пользователь с id {} удален", userId);
            return user;
        } else {
            log.info("Пользователь с id {} не найден", userId);
            return null;
        }
    }

    public void emailValidator(String email) {
        Collection<User> allUsersList = usersMap.values();

        allUsersList.forEach(user -> {
            if (user.getEmail().equals(email)) {
                log.error("Email пользователя: {} уже существует.", email);
                throw new DuplicatedDataException("Email пользователя уже существует.");
            }
        });
    }
}