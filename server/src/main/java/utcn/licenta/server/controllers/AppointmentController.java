package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.Appointment;
import utcn.licenta.server.models.DTO.AppointmentDTO;
import utcn.licenta.server.requests.DateRequest;
import utcn.licenta.server.services.AppointmentService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;

    @GetMapping("/{id}")
    public AppointmentDTO getOne(@PathVariable int id) {
        return appointmentService.getOne(id);
    }

    @GetMapping
    public List<AppointmentDTO> getAll() {
        return appointmentService.getAll();
    }

    @PostMapping
    public AppointmentDTO create(@RequestBody Appointment appointment) {
        return appointmentService.create(appointment);
    }

    @PutMapping("/{id}")
    public AppointmentDTO update(@PathVariable int id, @RequestBody Appointment appointment) {
        return appointmentService.update(id, appointment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        appointmentService.delete(id);
    }

    @PostMapping("/date")
    public List<AppointmentDTO> getAllByDate(@RequestBody DateRequest dateRequest) throws ParseException {
        return appointmentService.getAllByDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateRequest.getDate()));
    }

    @PostMapping("/date/client/{id}")
    public List<AppointmentDTO> getAllByDateAndClient(@RequestBody DateRequest dateRequest, @PathVariable int id) throws ParseException {
        return appointmentService.getAllByDateAndClient(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateRequest.getDate()), id);
    }

    @PostMapping("/date/trainer/{id}")
    public List<AppointmentDTO> getAllByDateAndTrainer(@RequestBody DateRequest dateRequest, @PathVariable int id) throws ParseException {
        return appointmentService.getAllByDateAndTrainer(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateRequest.getDate()), id);
    }
}
