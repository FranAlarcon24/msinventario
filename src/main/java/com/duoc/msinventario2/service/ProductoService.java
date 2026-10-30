package com.duoc.msinventario2.service;

import com.duoc.msinventario2.exception.ProductoNoEncontradoException;
import com.duoc.msinventario2.model.Producto;
import com.duoc.msinventario2.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public Producto crear(Producto producto) {
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        if (!repository.deleteById(id)) {
            throw new ProductoNoEncontradoException(id);
        }
    }
}