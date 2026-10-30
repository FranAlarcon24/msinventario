package com.duoc.msinventario2.service;

import com.duoc.msinventario2.exception.ProductoNoEncontradoException;
import com.duoc.msinventario2.model.Producto;
import com.duoc.msinventario2.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio: el repositorio se reemplaza por un mock (Mockito)
 * para probar solo la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - lógica de negocio")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private static Producto producto(Long id, String nombre, int stock, double precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setStock(stock);
        p.setPrecio(precio);
        return p;
    }

    @Test
    @DisplayName("Listar devuelve todos los productos del repositorio")
    void listar() {
        when(repository.findAll()).thenReturn(List.of(
                producto(1L, "Filtro de aceite", 10, 4990),
                producto(2L, "Bujía", 4, 2990)));

        assertThat(service.listar())
                .extracting(Producto::getNombre)
                .containsExactly("Filtro de aceite", "Bujía");
    }

    @Test
    @DisplayName("Obtener un producto existente lo devuelve")
    void obtenerExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto(1L, "Filtro de aceite", 10, 4990)));

        Producto encontrado = service.obtener(1L);

        assertThat(encontrado.getNombre()).isEqualTo("Filtro de aceite");
        assertThat(encontrado.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Caso crítico: obtener un producto inexistente lanza ProductoNoEncontradoException")
    void obtenerInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Crear guarda el producto en el repositorio y lo devuelve")
    void crear() {
        Producto nuevo = producto(null, "Pastillas de freno", 8, 15990);
        when(repository.save(nuevo)).thenReturn(producto(1L, "Pastillas de freno", 8, 15990));

        Producto creado = service.crear(nuevo);

        assertThat(creado.getId()).isEqualTo(1L);
        verify(repository).save(nuevo);
    }

    @Test
    @DisplayName("Eliminar un producto existente lo borra del repositorio")
    void eliminarExistente() {
        when(repository.deleteById(7L)).thenReturn(true);

        service.eliminar(7L);

        verify(repository).deleteById(7L);
    }

    @Test
    @DisplayName("Caso crítico: eliminar un producto inexistente lanza ProductoNoEncontradoException")
    void eliminarInexistente() {
        when(repository.deleteById(7L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(7L))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }
}
