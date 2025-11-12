package com.example.test.controllerV2Test;

import com.example.test.assemblers.ProductoModelAssembler;
import com.example.test.controller.ProductoControllerV2;
import com.example.test.model.Producto;
import com.example.test.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.argThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoControllerV2.class)
public class ProductoControllerV2Test {

    @Autowired
    private MockMvc mockMvc; // Injected MockMvc for making HTTP requests

    @MockBean // MockBean creates a mock and adds it to the Spring application context
    private ProductoService productoService;

    @MockBean
    private ProductoModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        producto1 = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        producto2 = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");

        when(assembler.toModel(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            return EntityModel.of(p,
                    linkTo(methodOn(ProductoControllerV2.class).getProductoByCodigo(p.getCode())).withSelfRel(),
                    linkTo(methodOn(ProductoControllerV2.class).getAllProductos()).withRel("productos"));
        });
    }

    @Test
    void getAllProductos() throws Exception {
        List<Producto> productos = Arrays.asList(producto1, producto2);
        when(productoService.getProductos()).thenReturn(productos);

        mockMvc.perform(get("/producto")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.productoList[0].id", is(producto1.getCode())))
                .andExpect(jsonPath("$._embedded.productoList[0].nombre", is(producto1.getName())))
                .andExpect(jsonPath("$._embedded.productoList[1].id", is(producto2.getCode())))
                .andExpect(jsonPath("$._embedded.productoList[1].nombre", is(producto2.getName())))
                .andExpect(jsonPath("$._links.self.href", containsString("/producto")))
                .andExpect(jsonPath("$._embedded.productoList[0]._links.self.href", containsString("/producto/" + producto1.getCode())))
                .andExpect(jsonPath("$._embedded.productoList[0]._links.productos.href", containsString("/producto")));

        verify(productoService, times(1)).getProductos();
        verify(assembler, times(2)).toModel(any(Producto.class));
    }

    @Test
    void getAllProductosNoProducts() throws Exception {
        when(productoService.getProductos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/producto")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist()) // No _embedded for empty collection
                .andExpect(jsonPath("$._links.self.href", containsString("/producto")));

        verify(productoService, times(1)).getProductos();
        verify(assembler, never()).toModel(any(Producto.class)); // Assembler should not be called for empty list
    }

    @Test
    void createProd() throws Exception {
        Producto newProductoRequest = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        Producto savedProducto = new Producto(5, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");

        when(productoService.agregarProducto(any(Producto.class))).thenReturn(savedProducto);
        // Assembler mock is handled in @BeforeEach

        mockMvc.perform(post("/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductoRequest))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(savedProducto.getCode())))
                .andExpect(jsonPath("$.nombre", is(savedProducto.getName())))
                .andExpect(jsonPath("$._links.self.href", containsString("/producto/" + savedProducto.getCode())))
                .andExpect(jsonPath("$._links.productos.href", containsString("/producto")));

        verify(productoService, times(1)).agregarProducto(any(Producto.class));
        verify(assembler, times(1)).toModel(savedProducto);
    }

    @Test
    void getProducto() throws Exception {
        when(productoService.buscarProducto(producto1.getCode())).thenReturn(producto1);

        mockMvc.perform(get("/producto/{id}", producto1.getName())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(producto1.getCode())))
                .andExpect(jsonPath("$.nombre", is(producto1.getName())))
                .andExpect(jsonPath("$._links.self.href", containsString("/producto/" + producto1.getCode())))
                .andExpect(jsonPath("$._links.productos.href", containsString("/producto")));

        verify(productoService, times(1)).buscarProducto(producto1.getCode());
        verify(assembler, times(1)).toModel(producto1);
    }

@Test
    void getProductoNotFound() throws Exception {
        int nonExistentId = 99;
        when(productoService.buscarProducto(nonExistentId)).thenThrow(new NoSuchElementException("Producto no encontrado con ID: " + nonExistentId));

        mockMvc.perform(get("/producto/{id}", nonExistentId)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Producto no encontrado con ID: " + nonExistentId)));

        verify(productoService, times(1)).buscarProducto(nonExistentId);
        verify(assembler, never()).toModel(any(Producto.class));
    }

    @Test
    void actualizarProd() throws Exception {
        Producto updatedInfo = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        Producto savedProducto = new Producto(producto1.getCode(), "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");

        when(productoService.modificaProducto(any(Producto.class))).thenReturn(savedProducto);

        mockMvc.perform(put("/producto/act/{id}", producto1.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedProducto.getCode())))
                .andExpect(jsonPath("$.nombre", is(savedProducto.getName())))
                .andExpect(jsonPath("$._links.self.href", containsString("/producto/" + savedProducto.getCode())))
                .andExpect(jsonPath("$._links.productos.href", containsString("/producto")));

        verify(productoService, times(1)).modificaProducto(argThat(p -> p.getCode() == producto1.getCode() && p.getName().equals("Bolsa Kraft Actualizada")));
        verify(assembler, times(1)).toModel(savedProducto);
    }

    @Test
    void actualizarProdNotFound() throws Exception {
        Producto updatedInfo = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        int nonExistentId = 99;

        when(productoService.modificaProducto(any(Producto.class)))
                .thenThrow(new NoSuchElementException("Producto no encontrado con ID: " + nonExistentId));

        mockMvc.perform(put("/producto/act/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedInfo))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNotFound()) // Expect 404 Not Found
                .andExpect(content().string(containsString("Producto no encontrado con ID: " + nonExistentId)));

        verify(productoService, times(1)).modificaProducto(any(Producto.class));
        verify(assembler, never()).toModel(any(Producto.class));
    }


    @Test
    void eliminarProd() throws Exception {
        doNothing().when(productoService).eliminarProducto(producto1.getCode());

        mockMvc.perform(delete("/producto/{id}", producto1.getCode())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminarProducto(producto1.getCode());
    }

    @Test
    void eliminarProdNotFound() throws Exception {
        int nonExistentId = 99;
        doThrow(new NoSuchElementException("Producto no encontrado con ID: " + nonExistentId))
                .when(productoService).eliminarProducto(nonExistentId);

        mockMvc.perform(delete("/producto/{id}", nonExistentId)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Producto no encontrado con ID: " + nonExistentId)));

        verify(productoService, times(1)).eliminarProducto(nonExistentId);
    }
}