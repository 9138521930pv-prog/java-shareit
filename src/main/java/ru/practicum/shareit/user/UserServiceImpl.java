package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDtoRequest) {
        validateUserDto(userDtoRequest);
        User user = userMapper.mapToUser(userDtoRequest);
        user = userRepository.save(user);
        log.info("Пользователь с id {} добавлен", user.getId());
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        log.info("Все пользователи получены.");
        return users.stream().map(userMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer userId) {
        validateUserById(userId);
        Optional<User> user = userRepository.findById(userId);
        log.info("Пользователь с id {} получен.", userId);
        return user.map(userMapper::mapToUserDto).orElse(null);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDtoRequest, Integer userId) {
        validateUserById(userId);

        User user = userRepository.findById(userId).get();

        if (userDtoRequest.getEmail() != null) {
            if (!Objects.equals(userDtoRequest.getEmail(), user.getEmail()) && userWithEmailExists(userDtoRequest.getEmail())) {
                throw new UserEmailAlreadyExistsException("Failed to update user. User with email " + userDtoRequest.getEmail() + " doesn't exist.");
            }
            user.setEmail(userDtoRequest.getEmail());
        }

        if (userDtoRequest.getName() != null) {
            user.setName(userDtoRequest.getName());
        }

        user = userRepository.save(user);
        log.info("Пользователь с id {} обновлен.", userId);
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        validateUserById(userId);
        userRepository.deleteById(userId);
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new ValidationException("Failed to process request. Item's name, description or isAvailable status must not be null.");
        }
    }

    private void validateUserById(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private boolean userWithEmailExists(String email) {
        return userRepository.findAll().stream().map(User::getEmail).anyMatch(email::equals);
    }
}