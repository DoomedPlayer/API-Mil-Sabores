package com.example.test.controllerTest;

import com.example.test.controller.ProductoController;
import com.example.test.model.Producto;
import com.example.test.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        producto1 = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        producto2 = new Producto(2, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
    }

    @Test
    void obtenerProductos() throws Exception {
        List<Producto> productos = Arrays.asList(producto1, producto2);
        when(productoService.getProductos()).thenReturn(productos);

        mockMvc.perform(get("/cotizacion/producto"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Bolsa Reutilizable"))
                .andExpect(jsonPath("$[1].nombre").value("Caja Carton"));

        verify(productoService, times(1)).getProductos();
    }

    @Test
    void obtenerProductosNoExisten() throws Exception {
        when(productoService.getProductos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cotizacion/producto"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).getProductos();
    }

    @Test
    void saveProducto() throws Exception {
        when(productoService.agregarProducto(any(Producto.class))).thenReturn(producto1);

        mockMvc.perform(post("/cotizacion/producto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Bolsa Reutilizable"));

        verify(productoService, times(1)).agregarProducto(any(Producto.class));
    }

    @Test
    void buscarProducto() throws Exception {
        when(productoService.buscarProducto(producto1.getCode())).thenReturn(producto1);

        mockMvc.perform(get("/cotizacion/producto/{id}", producto1.getCode()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Bolsa Reutilizable"));

        verify(productoService, times(1)).buscarProducto(producto1.getCode());
    }

    @Test
    void buscarProductoNoExiste() throws Exception {
        when(productoService.buscarProducto(any(Integer.class))).thenThrow(new RuntimeException("Producto no encontrado"));

        mockMvc.perform(get("/cotizacion/producto/{id}", 999))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).buscarProducto(any(Integer.class));
    }

    @Test
    void actualizarProducto() throws Exception {
        Producto productoActualizado = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");

        when(productoService.buscarProducto(producto1.getCode())).thenReturn(producto1);
        when(productoService.modificaProducto(any(Producto.class))).thenReturn(productoActualizado);

        mockMvc.perform(put("/cotizacion/producto/act/{id}", producto1.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Bolsa Reutilizable Mejorada"))
                .andExpect(jsonPath("$.precioUnitario").value(600));

        verify(productoService, times(1)).buscarProducto(producto1.getCode());
        verify(productoService, times(1)).modificaProducto(any(Producto.class));
    }

    @Test
    void actualizarProductoNoExiste() throws Exception {
        when(productoService.buscarProducto(any(Integer.class))).thenThrow(new RuntimeException("Producto no encontrado"));

        mockMvc.perform(put("/cotizacion/producto/act/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto1)))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).buscarProducto(any(Integer.class));
        verify(productoService, never()).modificaProducto(any(Producto.class));
    }

    @Test
    void eliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto(producto1.getCode());

        mockMvc.perform(delete("/cotizacion/producto/{id}", producto1.getCode()))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminarProducto(producto1.getCode());
    }

    @Test
    void eliminarProductoNoExiste() throws Exception {
        doThrow(new RuntimeException("Producto no encontrado")).when(productoService).eliminarProducto(any(Integer.class));

        mockMvc.perform(delete("/cotizacion/producto/{id}", 999))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).eliminarProducto(any(Integer.class));
    }
}
