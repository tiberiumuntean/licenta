package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.DTO.ReviewDTO;
import utcn.licenta.server.models.DTO.TrainerDTO;
import utcn.licenta.server.models.Review;
import utcn.licenta.server.repositories.ReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ReviewRepository reviewRepository;

    @Transactional
    public ReviewDTO getOne(int id) {
        Review review = reviewRepository.findById(id).orElse(null);
        return modelMapper.map(review, ReviewDTO.class);
    }

    @Transactional
    public List<ReviewDTO> getAll() {
        List<ReviewDTO> reviews = reviewRepository.findAll().stream().map(review -> modelMapper.map(review, ReviewDTO.class)).collect(Collectors.toList());

        return reviews;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReviewDTO create(Review review) {
        Review createdReview = reviewRepository.save(review);

        return modelMapper.map(createdReview, ReviewDTO.class);
    }

    @Transactional
    public ReviewDTO update(int id, Review newReview) {
        Review oldReview = reviewRepository.findById(id).orElse(null);

        oldReview.setTrainer(newReview.getTrainer());
        oldReview.setClient(newReview.getClient());
        oldReview.setReview(newReview.getReview());
        oldReview.setRating(newReview.getRating());

        Review createdReview = reviewRepository.save(oldReview);
        
        return modelMapper.map(createdReview, ReviewDTO.class);
    }

    @Transactional
    public void delete(int id) {
        reviewRepository.deleteById(id);
    }
}
