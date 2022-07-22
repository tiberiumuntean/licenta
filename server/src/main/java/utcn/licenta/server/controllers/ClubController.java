package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.Club;
import utcn.licenta.server.models.DTO.ClubDTO;
import utcn.licenta.server.services.ClubService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clubs")
public class ClubController {
    @Autowired
    ClubService clubService;

    @GetMapping("/{id}")
    public ClubDTO getOne(@PathVariable int id) {
        return clubService.getOne(id);
    }

    @GetMapping
    public List<ClubDTO> getAll() {
        return clubService.getAll();
    }

    @PostMapping
    public ClubDTO create(@RequestBody Club club) {
        return clubService.create(club);
    }

    @PutMapping("/{id}")
    public ClubDTO update(@PathVariable int id, @RequestBody Club club) {
        return clubService.update(id, club);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        clubService.delete(id);
    }
}
