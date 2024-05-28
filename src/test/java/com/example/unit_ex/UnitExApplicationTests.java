package com.example.unit_ex;

import controller.*;
import entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class UnitExApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void userControllerLoads() {
		assertThat(userController).isNotNull();
	}

	private User getUserFromId(Long id) throws Exception {
		MvcResult result = this.mockMvc.perform(get("/user/" + id))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		try {
			String userJSON = result.getResponse().getContentAsString();
			User user = objectMapper.readValue(userJSON, User.class);

			assertThat(user).isNotNull();
			assertThat(user.getId()).isNotNull();
			return user;

		} catch (Exception e) {
			return null;
		}
	}

	private User createUser() throws Exception {
		User user = new User();

		user.setName("Giulia");
		user.setSurname("Ciao");
		user.setEmail("giulia@gmail.com");
		return createUser(user);
	}

	private User createUser(User user) throws Exception {
		MvcResult result = createUserRequest(user);
		User userFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(userFromResponse).isNotNull();
		assertThat(userFromResponse.getId()).isNotNull();

		return userFromResponse;
	}

	private MvcResult createUserRequest() throws Exception {
		User user = new User();
		user.setName("Giulia");
		user.setSurname("Ciao");
		user.setEmail("giulia@gmail.com");

		return createUserRequest(user);
	}

	private MvcResult createUserRequest(User user) throws Exception {
		if (user == null) return null;
		String userJSON = objectMapper.writeValueAsString(user);

		return this.mockMvc.perform(post("/user/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

	}

	@Test
	void createUserTest() throws Exception {

		User userFromResponse = this.createUser();
	}

	@Test
	void readUsersList() throws Exception {
		createUserRequest();

		MvcResult result = this.mockMvc.perform(get("/user/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<User> usersFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
		System.out.println("user in database are: " + usersFromResponse.size());
		assertThat(usersFromResponse.size()).isNotZero();
	}

	@Test
	void readSingleUser() throws Exception {
		User student = this.createUser();
		User userFromResponse = getUserFromId(student.getId());
		assertThat(userFromResponse.getId()).isEqualTo(student.getId());

	}

	@Test
	void updateUser() throws Exception {
		User user = this.createUser();

		String newName = "Salvo";
		String newEmail = "salvo@gmail.com";
		user.setName(newName);
		user.setEmail(newEmail);
		String userJSON = objectMapper.writeValueAsString(user);


		MvcResult resultOne = this.mockMvc.perform(put("/user/set/" + user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		User userFromResponse = objectMapper.readValue(resultOne.getResponse().getContentAsString(), User.class);


		assertThat(userFromResponse.getId()).isEqualTo(user.getId());
		assertThat(userFromResponse.getName()).isEqualTo(newName);


		User userFromResponseGet = getUserFromId(user.getId());
		assertThat(userFromResponseGet.getId()).isEqualTo(user.getId());
		assertThat(userFromResponseGet.getName()).isEqualTo(newName);
		assertThat(userFromResponseGet.getEmail()).isEqualTo(newEmail);
	}

	@Test
	void deleteUser() throws Exception {
		User user = this.createUser();
		assertThat(user.getId()).isNotNull();

		this.mockMvc.perform(delete("/user/" + user.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		User userFromResponseGet = getUserFromId(user.getId());
		assertThat(userFromResponseGet).isNull();
	}
}
