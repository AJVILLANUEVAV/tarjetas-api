package com.nttdata.tarjetas.controller;

import com.nttdata.tarjetas.model.Tarjeta;
import com.nttdata.tarjetas.repository.TarjetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * API REST de tarjetas. Cada método atiende un endpoint.
 */
@RestController
@RequestMapping("/api/v1/tarjetas")
public class TarjetaController {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaController(TarjetaRepository tarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
    }

    /** GET /api/v1/tarjetas -> lista todas las tarjetas (200). */
    @GetMapping
    public List<Tarjeta> listarTarjetas() {
        return tarjetaRepository.listarTodas();
    }

    /** GET /api/v1/tarjetas/{numero} -> devuelve la tarjeta o 404 si no existe. */
    @GetMapping("/{numero}")
    public ResponseEntity<Tarjeta> buscarTarjetaPorNumero(@PathVariable String numero) {
        // Busca la tarjeta y responde con el estado correspondiente.
        Tarjeta tarjeta = tarjetaRepository.buscarPorNumero(numero);
        if (tarjeta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarjeta);
    }

    /** POST /api/v1/tarjetas -> valida y registra una tarjeta nueva. */
    @PostMapping
    public ResponseEntity<Tarjeta> registrarTarjeta(@RequestBody Tarjeta tarjeta) {
        // R1: el número debe tener 16 caracteres y el titular no puede estar vacío.
        if (tarjeta == null || tarjeta.getNumero() == null || tarjeta.getNumero().length() != 16
                || tarjeta.getTitular() == null || tarjeta.getTitular().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // R2: el tipo solo puede ser CREDITO o DEBITO.
        if (tarjeta.getTipo() == null
                || (!tarjeta.getTipo().equals("CREDITO") && !tarjeta.getTipo().equals("DEBITO"))) {
            return ResponseEntity.badRequest().build();
        }

        // R3: las tarjetas de crédito deben tener un límite mayor a cero.
        if (tarjeta.getTipo().equals("CREDITO") && !(tarjeta.getLimite() > 0)) {
            return ResponseEntity.badRequest().build();
        }

        // R4: no se permite registrar un número que ya exista.
        if (tarjetaRepository.buscarPorNumero(tarjeta.getNumero()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // R5: se guarda siempre como activa y se responde con estado 201.
        tarjeta.setEstado("ACTIVA");
        tarjetaRepository.guardar(tarjeta);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjeta);
    }

    /** PUT /api/v1/tarjetas/{numero}/bloquear -> bloquea la tarjeta o responde 404. */
    @PutMapping("/{numero}/bloquear")
    public ResponseEntity<Tarjeta> bloquearTarjeta(@PathVariable String numero) {
        // Busca la tarjeta indicada por su número.
        Tarjeta tarjeta = tarjetaRepository.buscarPorNumero(numero);
        if (tarjeta == null) {
            return ResponseEntity.notFound().build();
        }

        // Cambia el estado y devuelve la tarjeta actualizada.
        tarjeta.setEstado("BLOQUEADA");
        return ResponseEntity.ok(tarjeta);
    }
}
