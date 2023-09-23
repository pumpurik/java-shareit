package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void testGetUser() throws Exception {
        UserDto getUser = restTemplate.getForObject("http://localhost:" + port + "/users/1", UserDto.class);
        assertThat(getUser.getName()).isEqualTo("updateName");
        assertThat(getUser.getEmail()).isEqualTo("updateName@user.com");
    }

    @Test
    public void createUser() {
        UserDto userDto = new UserDto(1, "newName", "emailNew@user.com");
        UserDto getUser = restTemplate.postForObject("http://localhost:" + port + "/users", userDto, UserDto.class);

        assertThat(getUser.getName()).isEqualTo("newName");
        assertThat(getUser.getEmail()).isEqualTo("emailNew@user.com");
    }
}