package com.duoc.msinventario2;

import com.duoc.msinventario2.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración: levanta la aplicación completa (controlador + servicio +
 * repositorio reales) y recorre el ciclo de vida de un producto de punta a punta.
 */
@SpringBootTest
@DisplayName("Integración - ciclo de vida completo de un producto")
class ProductoIntegracionTest {

    @Autowired
    private WebApplicationContext contexto;

    @Autowired
    private ProductoRepository repository;

    private MockMvc mvc;

    @BeforeEach
    void configurar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        repository.deleteAll();
    }

    @Test
    @DisplayName("Crear, consultar, listar y eliminar un producto")
    void cicloDeVida() throws Exception {
        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Pastillas de freno","stock":8,"precio":15990.5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Pastillas de freno"));

        mvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(8));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(delete("/api/productos/1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/productos/1")).andExpect(status().isNotFound());
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Caso crítico: un producto inválido no se guarda (400)")
    void productoInvalidoNoSeGuarda() throws Exception {
        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Bujía","stock":-1,"precio":2990}
                                """))
                .andExpect(status().isBadRequest());

        assertThat(repository.findAll()).isEmpty();
    }
}
