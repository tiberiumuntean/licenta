package utcn.licenta.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.InvoiceDTO;
import utcn.licenta.server.models.Invoice;
import utcn.licenta.server.services.InvoiceService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    InvoiceService invoiceService;

    @GetMapping("/{id}")
    public InvoiceDTO getOne(@PathVariable int id) {
        return invoiceService.getOne(id);
    }

    @GetMapping
    public List<InvoiceDTO> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/client/{id}")
    public List<InvoiceDTO> getAllByClientId(@PathVariable int id) {
        return invoiceService.getAllByClientId(id);
    }

    @GetMapping("/trainer/{id}")
    public List<InvoiceDTO> getAllByTrainerId(@PathVariable int id) {
        return invoiceService.getAllByTrainerId(id);
    }

    @PostMapping
    public InvoiceDTO create(@RequestBody Invoice invoice) {
        return invoiceService.create(invoice);
    }

    @PutMapping("/{id}")
    public InvoiceDTO update(@PathVariable int id, Invoice invoice) {
        return invoiceService.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        invoiceService.delete(id);
    }
}
