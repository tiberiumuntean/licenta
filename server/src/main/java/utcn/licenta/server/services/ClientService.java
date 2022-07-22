package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Transactional
    public ClientDTO getOne(int id) {
        Client client = clientRepository.findById(id).orElse(null);
        return modelMapper.map(client, ClientDTO.class);
    }

    @Transactional
    public List<ClientDTO> getAll() {
        List<ClientDTO> clients = clientRepository.findAll().stream().map(client -> modelMapper.map(client, ClientDTO.class)).collect(Collectors.toList());

        /*for(ClientDTO client : clients){
            client.getInvoices().stream().map(invoice -> modelMapper.map(invoice, InvoiceDTO.class)).collect(Collectors.toList());
        }*/

        return clients;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClientDTO create(Client client) {
        Client createdClient = clientRepository.save(client);
        return modelMapper.map(createdClient, ClientDTO.class);
    }

    @Transactional
    public ClientDTO update(int id, Client newClient) {
        Client oldClient = clientRepository.findById(id).orElse(null);
        User oldUser = userRepository.findById(oldClient.getUser().getId()).orElse(null);
        Trainer trainer = null;
        if(newClient.getTrainer() != null) {
            trainer = trainerRepository.findById(newClient.getTrainer().getId()).orElse(null);
        }
        if(newClient.getTrainer_id() != null){
            trainer = trainerRepository.findById(newClient.getTrainer_id()).orElse(null);
        }
        User updatedUser = new User();

        // Actualizare copil (User)
        User newUser = newClient.getUser();

        if(newUser != null) {
            oldUser.setEmail(newUser.getEmail());
            oldUser.setPassword(newUser.getPassword());
            oldUser.setClubs(newUser.getClubs());
            oldUser.setTrainer(newUser.getTrainer());
            oldUser.setImage(newUser.getImage());

            updatedUser = userRepository.save(oldUser);
        }

        // Actualizare parinte (Client)
        oldClient.setFirstName(newClient.getFirstName());
        oldClient.setLastName(newClient.getLastName());
        oldClient.setAddress(newClient.getAddress());
        oldClient.setPhoneNumber(newClient.getPhoneNumber());
        oldClient.setBirthday(newClient.getBirthday());
        oldClient.setTrainer(trainer);
        oldClient.setClientMemberships(newClient.getClientMemberships());

        if(newUser != null) {
            oldClient.setUser(updatedUser);
        }

        Client updatedClient = clientRepository.save(oldClient);

        return modelMapper.map(updatedClient, ClientDTO.class);

    }

    @Transactional
    public void delete(int id) {
        clientRepository.deleteById(id);
    }
}
