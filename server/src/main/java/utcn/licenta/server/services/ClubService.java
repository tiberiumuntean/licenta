package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.Club;
import utcn.licenta.server.models.DTO.ClubDTO;
import utcn.licenta.server.repositories.ClubRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ClubRepository clubRepository;

    @Transactional
    public ClubDTO getOne(int id) {
        Club club = clubRepository.findById(id).orElse(null);

        return modelMapper.map(club, ClubDTO.class);
    }

    @Transactional
    public List<ClubDTO> getAll() {
        List<ClubDTO> clubs = clubRepository.findAll().stream().map(club -> modelMapper.map(club, ClubDTO.class)).collect(Collectors.toList());

        return clubs;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClubDTO create(Club club) {
        Club createdClub = clubRepository.save(club);
        return modelMapper.map(createdClub, ClubDTO.class);
    }

    @Transactional
    public ClubDTO update(int id, Club newClub) {
        Club oldClub = clubRepository.findById(id).orElse(null);

        oldClub.setName(newClub.getName());
        oldClub.setAddress(newClub.getAddress());
        oldClub.setPhoneNumber(newClub.getPhoneNumber());
        oldClub.setEmail(newClub.getEmail());
        oldClub.setSchedule(newClub.getSchedule());
        oldClub.setLatitude(newClub.getLatitude());
        oldClub.setLongitude(newClub.getLongitude());
        oldClub.setUsers(newClub.getUsers());
        oldClub.setMemberships(newClub.getMemberships());

        Club updatedClub = clubRepository.save(oldClub);

        return modelMapper.map(updatedClub, ClubDTO.class);
    }

    @Transactional
    public void delete(int id) {
        clubRepository.deleteById(id);
    }
}
