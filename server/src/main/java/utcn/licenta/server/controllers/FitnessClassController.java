package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.FitnessClassDTO;
import utcn.licenta.server.models.FitnessClass;
import utcn.licenta.server.requests.DateRequest;
import utcn.licenta.server.services.FitnessClassService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/classes")
public class FitnessClassController {
    @Autowired
    FitnessClassService fitnessClassService;

    @GetMapping("/{id}")
    public FitnessClassDTO getOne(@PathVariable int id) {
        return fitnessClassService.getOne(id);
    }

    @PostMapping("/date")
    public List<FitnessClassDTO> getAllByDate(@RequestBody DateRequest dateRequest) throws ParseException {
        return fitnessClassService.getAllByDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateRequest.getDate()));
    }

    @GetMapping
    public List<FitnessClassDTO> getAll() {
        return fitnessClassService.getAll();
    }

    @PostMapping
    public FitnessClassDTO create(@RequestBody FitnessClass fitnessClass) {
        return fitnessClassService.create(fitnessClass);
    }

    @PutMapping("/{id}")
    public FitnessClassDTO update(@PathVariable int id, @RequestBody FitnessClass fitnessClass) {
        return fitnessClassService.update(id, fitnessClass);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        fitnessClassService.delete(id);
    }
}
