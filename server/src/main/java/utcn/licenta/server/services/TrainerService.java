package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.DTO.ReviewDTO;
import utcn.licenta.server.models.DTO.TrainerDTO;
import utcn.licenta.server.models.Trainer;
import utcn.licenta.server.repositories.TrainerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TrainerRepository trainerRepository;

    @Transactional
    public TrainerDTO getOne(int id) {
        Trainer trainer = trainerRepository.findById(id).orElse(null);
        return modelMapper.map(trainer, TrainerDTO.class);
    }

    @Transactional
    public List<TrainerDTO> getAll() {
        List<TrainerDTO> trainers = trainerRepository.findAll().stream().map(trainer -> modelMapper.map(trainer, TrainerDTO.class)).collect(Collectors.toList());

        for(TrainerDTO trainer : trainers){
            trainer.getReviews().stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());
        }

        return trainers;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TrainerDTO create(Trainer trainer) {
        Trainer createdTrainer = trainerRepository.save(trainer);

        return modelMapper.map(createdTrainer, TrainerDTO.class);
    }

    @Transactional
    public TrainerDTO update(int id, Trainer newTrainer) {
        Trainer oldTrainer = trainerRepository.findById(id).orElse(null);

        oldTrainer.setFirstName(newTrainer.getFirstName());
        oldTrainer.setLastName(newTrainer.getLastName());
        oldTrainer.setAddress(newTrainer.getAddress());
        oldTrainer.setPhoneNumber(newTrainer.getPhoneNumber());
        oldTrainer.setFreeSpots(newTrainer.getFreeSpots());
        oldTrainer.setWorkExperience(newTrainer.getWorkExperience());
        oldTrainer.setDescription(newTrainer.getDescription());
        oldTrainer.setInstagram(newTrainer.getInstagram());
        oldTrainer.setFacebook(newTrainer.getFacebook());
        oldTrainer.setPrice(newTrainer.getPrice());

        Trainer updatedTrainer = trainerRepository.save(oldTrainer);

        return modelMapper.map(updatedTrainer, TrainerDTO.class);
    }

    @Transactional
    public void delete(int id) {
        trainerRepository.deleteById(id);
    }
}
