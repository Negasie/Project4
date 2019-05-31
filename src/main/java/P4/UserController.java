package P4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;


@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/users")
    public Iterable<User> usersIndex(){
        return userRepository.findAll();
    }

    //POST
    @PostMapping("/users")
    public User createUser(@RequestBody User user, HttpSession session){
        User newUser = userService.saveUser(user);
            session.setAttribute("username", newUser.getUsername());

        return newUser;
    }
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        userRepository.deleteById(id);
        return "deleted user " + id;
    }
    @PutMapping("/users/{id}")
    public User updateUser(@RequestBody User formData, @PathVariable("id") Long id) throws Exception{
        Optional<User> response = userRepository.findById(id);
        if(response.isPresent()){
            User user = response.get();
            user.setLocation(formData.getLocation());
            return userRepository.save(user);
        }
        throw new Exception("no such user");
    }

    // as another route listed after class variables
    @PostMapping("/users/login")
    public User login(@RequestBody User login, HttpSession session) throws IOException {

        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String enteredUsername = login.getUsername();

        System.out.println(enteredUsername);

        User user = userRepository.findByUsername(login.getUsername());

        System.out.println(user);

        if(user ==  null){
            throw new IOException("Invalid Credentials");
        }

        boolean valid = bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword());

        if(valid){
            session.setAttribute("username", user.getUsername());
            return user;

        }else{
            throw new IOException("Invalid Credentials");
        }
    }




}