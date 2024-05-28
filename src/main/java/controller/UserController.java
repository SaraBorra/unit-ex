package controller;

import entity.*;
import repo.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public @ResponseBody User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/")
    public @ResponseBody List<User> getAllStudents() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public @ResponseBody User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/set/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User newUser) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setName(newUser.getName());
            user.setSurname(newUser.getSurname());
            user.setEmail(newUser.getEmail());
            return userRepository.save(user);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
