package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.Client;
import utcn.licenta.server.models.ClientMembership;
import utcn.licenta.server.models.DTO.ClientDTO;
import utcn.licenta.server.models.DTO.ClientMembershipDTO;
import utcn.licenta.server.services.ClientMembershipService;
import utcn.licenta.server.services.ClientService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/client-memberships")
public class ClientMembershipController {
    @Autowired
    ClientMembershipService clientMembershipService;

    @GetMapping("/{id}")
    public ClientMembershipDTO getOne(@PathVariable int id) {
        return clientMembershipService.getOne(id);
    }

    @GetMapping
    public List<ClientMembershipDTO> getAll() {
        return clientMembershipService.getAll();
    }

    @PostMapping
    public ClientMembershipDTO create(@RequestBody ClientMembership clientMembership) {
        return clientMembershipService.create(clientMembership);
    }

    @PutMapping("/{id}")
    public ClientMembershipDTO update(@PathVariable int id, @RequestBody ClientMembership clientMembership) {
        return clientMembershipService.update(id, clientMembership);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        clientMembershipService.delete(id);
    }
}
