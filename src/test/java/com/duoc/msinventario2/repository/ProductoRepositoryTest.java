package com.duoc.msinventario2.repository;

import com.duoc.msinventario2.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del repositorio en memoria (sin Spring ni mocks).
 */
@DisplayName("ProductoRepository - almacenamiento en memoria")
class ProductoRepositoryTest {

    private ProductoRepository repository;

    @BeforeEach
    void configurar() {
        repository = new ProductoRepository();
    }

    private static Producto producto(String nombre) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setStock(1);
        p.setPrecio(1000);
        return p;
    }

    @Test
    @DisplayName("Guardar asigna ids correlativos")
    void guardarAsignaIds() {
        Producto a = repository.save(producto("A"));
        Producto b = repository.save(producto("B"));

        assertThat(a.getId()).isEqualTo(1L);
        assertThat(b.getId()).isEqualTo(2L);
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Buscar por id devuelve el producto o vacío si no existe")
    void buscarPorId() {
        Producto guardado = repository.save(producto("Filtro"));

        assertThat(repository.findById(guardado.getId())).contains(guardado);
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("Eliminar devuelve true si existía y false si no")
    void eliminar() {
        Producto guardado = repository.save(producto("Bujía"));

        assertThat(repository.deleteById(guardado.getId())).isTrue();
        assertThat(repository.deleteById(guardado.getId())).isFalse();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Vaciar el repositorio reinicia la secuencia de ids")
    void vaciar() {
        repository.save(producto("A"));
        repository.save(producto("B"));

        repository.deleteAll();

        assertThat(repository.findAll()).isEmpty();
        assertThat(repository.save(producto("C")).getId()).isEqualTo(1L);
    }
}
