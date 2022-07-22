package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.ReviewDTO;
import utcn.licenta.server.models.Review;
import utcn.licenta.server.services.ReviewService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @GetMapping("/{id}")
    public ReviewDTO getOne(@PathVariable int id) {
        return reviewService.getOne(id);
    }

    @GetMapping
    public List<ReviewDTO> getAll() {
        return reviewService.getAll();
    }

    @PostMapping
    public ReviewDTO create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping("/{id}")
    public ReviewDTO update(@PathVariable int id, Review review) {
        return reviewService.update(id, review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        reviewService.delete(id);
    }
}
