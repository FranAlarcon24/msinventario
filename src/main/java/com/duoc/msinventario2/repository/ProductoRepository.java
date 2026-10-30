package com.duoc.msinventario2.repository;

import com.duoc.msinventario2.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductoRepository {

    private final Map<Long, Producto> datos = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    public List<Producto> findAll() {
        return new ArrayList<>(datos.values());
    }

    public Optional<Producto> findById(Long id) {
        return Optional.ofNullable(datos.get(id));
    }

    public Producto save(Producto producto) {
        producto.setId(secuencia.incrementAndGet());
        datos.put(producto.getId(), producto);
        return producto;
    }

    public boolean deleteById(Long id) {
        return datos.remove(id) != null;
    }

    public void deleteAll() {
        datos.clear();
        secuencia.set(0);
    }
}