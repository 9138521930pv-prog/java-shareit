package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldMapToUserDto() {
        User user = User.builder().id(1).name("Add").email("@Add.com").build();
        UserDto mapUserDto = userMapper.mapToUserDto(user);
        Assertions.assertNotNull(mapUserDto);
        Assertions.assertEquals(1, mapUserDto.getId());
        Assertions.assertEquals("Add", mapUserDto.getName());
        Assertions.assertEquals("@Add.com", mapUserDto.getEmail());
    }
}
