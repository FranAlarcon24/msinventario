package com.duoc.msinventario2.controller;

import com.duoc.msinventario2.exception.ProductoNoEncontradoException;
import com.duoc.msinventario2.model.Producto;
import com.duoc.msinventario2.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del contrato HTTP del controlador con MockMvc: códigos de estado,
 * JSON de respuesta y validaciones. El servicio se reemplaza por un mock.
 */
@DisplayName("ProductoController - contrato HTTP")
class ProductoControllerTest {

    private ProductoService service;
    private MockMvc mvc;

    @BeforeEach
    void configurar() {
        service = mock(ProductoService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ProductoController(service)).build();
    }

    private static Producto producto() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Filtro de aceite");
        p.setStock(10);
        p.setPrecio(4990);
        return p;
    }

    @Test
    @DisplayName("GET /api/productos responde 200 con la lista")
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(producto()));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Filtro de aceite"))
                .andExpect(jsonPath("$[0].stock").value(10));
    }

    @Test
    @DisplayName("GET /api/productos/{id} existente responde 200")
    void obtener() throws Exception {
        when(service.obtener(1L)).thenReturn(producto());

        mvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/productos/{id} inexistente responde 404")
    void obtenerInexistente() throws Exception {
        when(service.obtener(99L)).thenThrow(new ProductoNoEncontradoException(99L));

        mvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST válido responde 201 con el producto creado")
    void crearValido() throws Exception {
        when(service.crear(any(Producto.class))).thenReturn(producto());

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Filtro de aceite","stock":10,"precio":4990}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Caso crítico: POST con datos inválidos responde 400 y no llama al servicio")
    void crearInvalido() throws Exception {
        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"","stock":-3,"precio":0}
                                """))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any());
    }

    @Test
    @DisplayName("DELETE existente responde 204")
    void eliminar() throws Exception {
        mvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE inexistente responde 404")
    void eliminarInexistente() throws Exception {
        doThrow(new ProductoNoEncontradoException(5L)).when(service).eliminar(5L);

        mvc.perform(delete("/api/productos/5"))
                .andExpect(status().isNotFound());
    }
}
