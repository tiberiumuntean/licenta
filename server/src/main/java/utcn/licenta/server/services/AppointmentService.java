package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.Appointment;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.DTO.AppointmentDTO;
import utcn.licenta.server.models.FitnessClass;
import utcn.licenta.server.models.Trainer;
import utcn.licenta.server.repositories.AppointmentRepository;
import utcn.licenta.server.repositories.ClientRepository;
import utcn.licenta.server.repositories.TrainerRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TrainerRepository trainerRepository;

    @Transactional
    public AppointmentDTO getOne(int id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    @Transactional
    public List<AppointmentDTO> getAll() {
        List<AppointmentDTO> appointments = appointmentRepository.findAll().stream().map(appointment -> modelMapper.map(appointment, AppointmentDTO.class)).collect(Collectors.toList());
        return appointments;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AppointmentDTO create(Appointment appointment) {
        Appointment createdAppointment = appointmentRepository.save(appointment);
        return modelMapper.map(createdAppointment, AppointmentDTO.class);
    }

    @Transactional
    public AppointmentDTO update(int id, Appointment newAppointment) {
        Appointment oldAppointment = appointmentRepository.findById(id).orElse(null);

        oldAppointment.setName(newAppointment.getName());
        oldAppointment.setDate(newAppointment.getDate());

        Appointment updatedAppointment = appointmentRepository.save(oldAppointment);

        return modelMapper.map(updatedAppointment, AppointmentDTO.class);
    }

    @Transactional
    public void delete(int id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public List<AppointmentDTO> getAllByDate(Date date) {
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

        return appointmentRepository.findAllByDateBetween(todayDate, tomorrowDate).stream().map(appointment -> modelMapper.map(appointment, AppointmentDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentDTO> getAllByDateAndClient(Date date, int id) {
        Client client = clientRepository.findById(id).orElse(null);

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

        return appointmentRepository.findAllByDateBetweenAndClient(todayDate, tomorrowDate, client).stream().map(appointment -> modelMapper.map(appointment, AppointmentDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<AppointmentDTO> getAllByDateAndTrainer(Date date, int id) {
        Trainer trainer = trainerRepository.findById(id).orElse(null);

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

        return appointmentRepository.findAllByDateBetweenAndTrainer(todayDate, tomorrowDate, trainer).stream().map(appointment -> modelMapper.map(appointment, AppointmentDTO.class)).collect(Collectors.toList());
    }
}
