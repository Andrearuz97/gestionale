package com.gestionale.controller;

import com.gestionale.entity.Cliente;
import com.gestionale.service.ClienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public Cliente crea(@RequestBody Cliente cliente) {
        return service.salva(cliente);
    }

    @GetMapping
    public List<Cliente> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void cancella(@PathVariable Long id) {
        service.cancella(id);
    }
}
