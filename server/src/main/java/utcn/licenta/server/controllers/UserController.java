package utcn.licenta.server.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.UserDTO;
import utcn.licenta.server.models.User;
import utcn.licenta.server.repositories.UserRepository;
import utcn.licenta.server.requests.LoginRequest;
import utcn.licenta.server.requests.PasswordResetRequest;
import utcn.licenta.server.requests.SignupRequest;
import utcn.licenta.server.responses.JwtResponse;
import utcn.licenta.server.responses.PasswordResetResponse;
import utcn.licenta.server.security.jwt.JwtUtils;
import utcn.licenta.server.services.UserService;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), user.getClubs(), user.getRole()));
    }

    @PostMapping("/signup")
    public User registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (!userRepository.existsByEmail(signUpRequest.getEmail())) {
            User user = new User(signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getRole());

            userRepository.save(user);

            return user;
        } else {
            throw new BadCredentialsException("The email already exists in the database!");
        }
    }

    @GetMapping("/{id}")
    public UserDTO getOne(@PathVariable int id) {
        return userService.getOne(id);
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public UserDTO create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable int id, @RequestBody User user) throws IOException {
        return userService.update(id, user);
    }

    @PostMapping("/password-reset")
    public PasswordResetResponse updatePassword(@RequestBody PasswordResetRequest request) {
        User user = userRepository.findById(request.getId()).orElse(null);

        if (encoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return new PasswordResetResponse(200, "The password has been successfully updated!");
        } else {
            return new PasswordResetResponse(500, "The current password you've entered doesn't match the one from the database");
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping(value = "/{id}/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@PathVariable int id) {
        InputStream in = null;
        try {
            in = new FileInputStream(String.format("avatars/user_avatar_%d.jpg", id));
            return IOUtils.toByteArray(in);

        } catch (Exception ignored) {
        }

        return null;
    }
}
