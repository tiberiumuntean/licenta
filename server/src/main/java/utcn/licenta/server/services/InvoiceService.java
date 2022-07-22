package utcn.licenta.server.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import utcn.licenta.server.models.DTO.InvoiceDTO;
import utcn.licenta.server.models.Invoice;
import utcn.licenta.server.repositories.InvoiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Transactional
    public InvoiceDTO getOne(int id) {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);

        return modelMapper.map(invoice, InvoiceDTO.class);
    }

    @Transactional
    public List<InvoiceDTO> getAll() {
        return invoiceRepository.findAll().stream().map(invoice -> modelMapper.map(invoice, InvoiceDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<InvoiceDTO> getAllByClientId(int id) {
        return invoiceRepository.findAllByClientId(id).stream().map(invoice -> modelMapper.map(invoice, InvoiceDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<InvoiceDTO> getAllByTrainerId(int id) {
        return invoiceRepository.findAllByTrainerId(id).stream().map(invoice -> modelMapper.map(invoice, InvoiceDTO.class)).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InvoiceDTO create(Invoice invoice) {
        Invoice createdInvoice = invoiceRepository.save(invoice);

        return modelMapper.map(createdInvoice, InvoiceDTO.class);
    }

    @Transactional
    public InvoiceDTO update(int id, Invoice newInvoice) {
        Invoice oldInvoice = invoiceRepository.findById(id).orElse(null);

        oldInvoice.setDate(newInvoice.getDate());
        oldInvoice.setNumber(newInvoice.getNumber());

        Invoice updatedInvoice = invoiceRepository.save(oldInvoice);

        return modelMapper.map(updatedInvoice, InvoiceDTO.class);
    }

    @Transactional
    public void delete(int id) {
        invoiceRepository.deleteById(id);
    }
}
