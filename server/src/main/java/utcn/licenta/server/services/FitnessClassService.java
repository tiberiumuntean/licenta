package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.Club;
import utcn.licenta.server.models.DTO.FitnessClassDTO;
import utcn.licenta.server.models.FitnessClass;
import utcn.licenta.server.repositories.ClientRepository;
import utcn.licenta.server.repositories.ClubRepository;
import utcn.licenta.server.repositories.FitnessClassRepository;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FitnessClassService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    FitnessClassRepository fitnessClassRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    ClubRepository clubRepository;

    @Transactional
    public FitnessClassDTO getOne(int id) {
        FitnessClass fitnessClass = fitnessClassRepository.findById(id).orElse(null);

        return modelMapper.map(fitnessClass, FitnessClassDTO.class);
    }

    @Transactional
    public List<FitnessClassDTO> getAll() {
        return fitnessClassRepository.findAll().stream().map(fitnessClass -> modelMapper.map(fitnessClass, FitnessClassDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<FitnessClassDTO> getAllByDate(Date date) {
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(date);
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        Date todayDate = todayCalendar.getTime();

        Calendar nextWeekCalendar = Calendar.getInstance();
        nextWeekCalendar.setTime(todayDate);
        nextWeekCalendar.add(Calendar.DATE, 7);
        Date tomorrowDate = nextWeekCalendar.getTime();

        return fitnessClassRepository.findAllByDateBetween(todayDate, tomorrowDate).stream().map(fitnessClass -> modelMapper.map(fitnessClass, FitnessClassDTO.class)).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FitnessClassDTO create(FitnessClass fitnessClass) {
        Club club = clubRepository.findById(fitnessClass.getClub_id()).orElse(null);
        fitnessClass.setClub(club);
        FitnessClass createdFitnessClass = fitnessClassRepository.save(fitnessClass);

        // club.addFitnessClass(createdFitnessClass);
        //clubRepository.save(club);

        return modelMapper.map(createdFitnessClass, FitnessClassDTO.class);
    }

    @Transactional
    public FitnessClassDTO update(int id, FitnessClass newFitnessClass) {
        FitnessClass oldFitnessClass = fitnessClassRepository.findById(id).orElse(null);

        oldFitnessClass.setName(newFitnessClass.getName());
        oldFitnessClass.setDate(newFitnessClass.getDate());
        oldFitnessClass.setDescription(newFitnessClass.getDescription());
        oldFitnessClass.setFreeSpots(newFitnessClass.getFreeSpots());
        oldFitnessClass.setLocation(newFitnessClass.getLocation());
        oldFitnessClass.setClients(newFitnessClass.getClients());

        FitnessClass updatedFitnessClass = fitnessClassRepository.save(oldFitnessClass);

        return modelMapper.map(updatedFitnessClass, FitnessClassDTO.class);
    }

    @Transactional
    public void delete(int id) {
        fitnessClassRepository.deleteById(id);
    }
}
