package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.ClientMembership;
import utcn.licenta.server.models.DTO.ClientDTO;
import utcn.licenta.server.models.DTO.ClientMembershipDTO;
import utcn.licenta.server.models.Trainer;
import utcn.licenta.server.models.User;
import utcn.licenta.server.repositories.ClientMembershipRepository;
import utcn.licenta.server.repositories.ClientRepository;
import utcn.licenta.server.repositories.TrainerRepository;
import utcn.licenta.server.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientMembershipService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ClientMembershipRepository clientMembershipRepository;

    @Transactional
    public ClientMembershipDTO getOne(int id) {
        ClientMembership clientMembership = clientMembershipRepository.findById(id).orElse(null);
        return modelMapper.map(clientMembership, ClientMembershipDTO.class);
    }

    @Transactional
    public List<ClientMembershipDTO> getAll() {
        List<ClientMembershipDTO> clientMemberships = clientMembershipRepository.findAll().stream().map(clientMembership -> modelMapper.map(clientMembership, ClientMembershipDTO.class)).collect(Collectors.toList());

        return clientMemberships;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClientMembershipDTO create(ClientMembership clientMembership) {
        ClientMembership createdClientMembership = clientMembershipRepository.save(clientMembership);
        return modelMapper.map(createdClientMembership, ClientMembershipDTO.class);
    }

    @Transactional
    public ClientMembershipDTO update(int id, ClientMembership newClientMembership) {
        ClientMembership oldClientMembership = clientMembershipRepository.findById(id).orElse(null);

        oldClientMembership.setClient(newClientMembership.getClient());
        oldClientMembership.setMembership(newClientMembership.getMembership());
        oldClientMembership.setMembershipStartDate(newClientMembership.getMembershipStartDate());
        oldClientMembership.setMembershipEndDate(newClientMembership.getMembershipEndDate());

        ClientMembership updatedClientMembership = clientMembershipRepository.save(oldClientMembership);

        return modelMapper.map(updatedClientMembership, ClientMembershipDTO.class);
    }

    @Transactional
    public void delete(int id) {
        clientMembershipRepository.deleteById(id);
    }
}
