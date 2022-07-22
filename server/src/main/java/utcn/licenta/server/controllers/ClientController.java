package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.DTO.ClientDTO;
import utcn.licenta.server.services.ClientService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    ClientService clientService;

    @GetMapping("/{id}")
    public ClientDTO getOne(@PathVariable int id) {
        return clientService.getOne(id);
    }

    @GetMapping
    public List<ClientDTO> getAll() {
        return clientService.getAll();
    }

    @PostMapping
    public ClientDTO create(@RequestBody Client client) {
        return clientService.create(client);
    }

    @PutMapping("/{id}")
    public ClientDTO update(@PathVariable int id, @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        clientService.delete(id);
    }

    /*@GetMapping("/user/{id}")
    public Client getClientByUserId(@PathVariable int id){
        return clientService.getClientByUserId(id);
    }*/
}
