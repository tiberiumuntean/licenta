package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.TrainerDTO;
import utcn.licenta.server.models.Trainer;
import utcn.licenta.server.services.TrainerService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    @Autowired
    TrainerService trainerService;

    @GetMapping("/{id}")
    public TrainerDTO getOne(@PathVariable int id) {
        return trainerService.getOne(id);
    }

    @GetMapping
    public List<TrainerDTO> getAll() {
        return trainerService.getAll();
    }

    @PostMapping
    public TrainerDTO create(@RequestBody Trainer trainer) {
        return trainerService.create(trainer);
    }

    @PutMapping("/{id}")
    public TrainerDTO update(@PathVariable int id, @RequestBody Trainer trainer) {
        return trainerService.update(id, trainer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        trainerService.delete(id);
    }

}
