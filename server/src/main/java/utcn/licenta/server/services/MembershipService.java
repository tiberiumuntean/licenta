package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.DTO.MembershipDTO;
import utcn.licenta.server.models.Membership;
import utcn.licenta.server.repositories.MembershipRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MembershipRepository membershipRepository;

    @Transactional
    public MembershipDTO getOne(int id) {
        Membership membership = membershipRepository.findById(id).orElse(null);

        return modelMapper.map(membership, MembershipDTO.class);
    }

    @Transactional
    public List<MembershipDTO> getAll() {
        return membershipRepository.findAll().stream().map(membership -> modelMapper.map(membership, MembershipDTO.class)).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MembershipDTO create(Membership membership) {
        Membership createdMembership = membershipRepository.save(membership);

        return modelMapper.map(createdMembership, MembershipDTO.class);
    }

    @Transactional
    public MembershipDTO update(int id, Membership newMembership) {
        Membership oldMembership = membershipRepository.findById(id).orElse(null);

        oldMembership.setName(newMembership.getName());
        oldMembership.setPrice(newMembership.getPrice());
        oldMembership.setClub(newMembership.getClub());
        oldMembership.setDuration(newMembership.getDuration());

        Membership updatedMembership = membershipRepository.save(oldMembership);

        return modelMapper.map(updatedMembership, MembershipDTO.class);
    }

    @Transactional
    public void delete(int id) {
        membershipRepository.deleteById(id);
    }
}
