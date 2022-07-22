package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.DTO.*;
import utcn.licenta.server.models.Trainer;
import utcn.licenta.server.models.User;
import utcn.licenta.server.repositories.ClientRepository;
import utcn.licenta.server.repositories.TrainerRepository;
import utcn.licenta.server.repositories.UserRepository;
import utcn.licenta.server.requests.PasswordResetRequest;
import utcn.licenta.server.responses.PasswordResetResponse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Transactional
    public UserDTO getOne(int id) {
        User user = userRepository.findById(id).orElse(null);

        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public List<UserDTO> getAll() {
        List<UserDTO> users = userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());

        return users;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDTO create(User user) {
        User createdUser = userRepository.save(user);
        return modelMapper.map(createdUser, UserDTO.class);
    }

    @Transactional
    public UserDTO update(int id, User newUser) throws IOException {
        User oldUser = userRepository.findById(id).orElse(null);

        if(oldUser.getRole() != 2) {
            if (newUser.getClient() == null) {
                Trainer oldTrainer = trainerRepository.findById(newUser.getTrainer().getId()).orElse(null);

                // Actualizare copil (Trainer)
                Trainer newTrainer = newUser.getTrainer();
                oldTrainer.setFirstName(newTrainer.getFirstName());
                oldTrainer.setLastName(newTrainer.getLastName());
                oldTrainer.setAddress(newTrainer.getAddress());
                oldTrainer.setPhoneNumber(newTrainer.getPhoneNumber());
                oldTrainer.setDescription(newTrainer.getDescription());
                oldTrainer.setFacebook(newTrainer.getFacebook());
                oldTrainer.setInstagram(newTrainer.getInstagram());
                oldTrainer.setFreeSpots(newTrainer.getFreeSpots());
                oldTrainer.setWorkExperience(newTrainer.getWorkExperience());
                oldTrainer.setPrice(newTrainer.getPrice());

                Trainer updatedTrainer = trainerRepository.save(oldTrainer);
                oldUser.setTrainer(updatedTrainer);
            } else {
                Client oldClient = clientRepository.findById(newUser.getClient().getId()).orElse(null);

                Trainer trainer = null;

                if (newUser.getClient().getTrainer() != null) {
                    trainer = trainerRepository.findById(newUser.getClient().getTrainer_id()).orElse(null);
                }

                if (newUser.getClient().getTrainer_id() != null) {
                    trainer = trainerRepository.findById(newUser.getClient().getTrainer_id()).orElse(null);
                }

                // Actualizare copil (Client)
                Client newClient = newUser.getClient();
                oldClient.setFirstName(newClient.getFirstName());
                oldClient.setLastName(newClient.getLastName());
                oldClient.setAddress(newClient.getAddress());
                oldClient.setPhoneNumber(newClient.getPhoneNumber());
                oldClient.setBirthday(newClient.getBirthday());
                oldClient.setTrainer(trainer);

                Client updatedClient = clientRepository.save(oldClient);
                oldUser.setClient(updatedClient);
            }
        }

        // Actualizare parinte (User)
        oldUser.setEmail(newUser.getEmail());
        oldUser.setPassword(newUser.getPassword());
        oldUser.setClubs(newUser.getClubs());
        oldUser.setTrainer(newUser.getTrainer());

        if(newUser.getImage() != null){
            byte[] decodedImg = Base64.getMimeDecoder().decode(newUser.getImage().getBytes(StandardCharsets.UTF_8));

            if(oldUser.getImage() != null) {
                byte[] decodedImgOld = Base64.getMimeDecoder().decode(oldUser.getImage().getBytes(StandardCharsets.UTF_8));
                if(decodedImgOld.length != decodedImg.length) {
                    fileWithDirectoryAssurance("avatars", String.format("user_avatar_%d.jpg", oldUser.getId()));
                    Path destinationFile = Paths.get("avatars", String.format("user_avatar_%d.jpg", oldUser.getId()));
                    Files.write(destinationFile, decodedImg);
                    oldUser.setImage(String.format("user_avatar_%d.jpg", oldUser.getId()));
                }
            } else {
                fileWithDirectoryAssurance("avatars", String.format("user_avatar_%d.jpg", oldUser.getId()));
                Path destinationFile = Paths.get("avatars", String.format("user_avatar_%d.jpg", oldUser.getId()));
                Files.write(destinationFile, decodedImg);
                oldUser.setImage(String.format("user_avatar_%d.jpg", oldUser.getId()));
            }
        }

        User createdUser = userRepository.save(oldUser);

        return modelMapper.map(createdUser, UserDTO.class);
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with email: " + email);
        }

        return User.build(user);
    }

    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
        return new File(directory + File.separatorChar + filename);
    }
}
