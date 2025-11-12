package com.example.test.serviceTest;

import com.example.test.model.Producto;
import com.example.test.repository.ProductoRepositoryI;
import com.example.test.services.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepositoryI productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductos() {
        Producto producto1 = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        Producto producto2 = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> result = productoService.getProductos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(producto1.getName(), result.get(0).getName());
        assertEquals(producto2.getPrice(), result.get(1).getPrice());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void buscarProducto() {
        int id = 1;
        Producto producto = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        Producto result = productoService.buscarProducto(id);

        assertNotNull(result);
        assertEquals(id, result.getCode());
        assertEquals("Bolsa A", result.getName());
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    void buscarProductoNoExiste() {
        int id = 99;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> productoService.buscarProducto(id));
        verify(productoRepository, times(1)).findById(id);
    }

    @Test
    void agregarProducto() {
        Producto nuevoProducto = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        Producto productoGuardado = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto result = productoService.agregarProducto(nuevoProducto);

        assertNotNull(result);
        assertEquals(3, result.getCode());
        assertEquals("Nuevo Producto", result.getName());
        assertEquals(1000, result.getPrice());
        verify(productoRepository, times(1)).save(nuevoProducto);
    }

    @Test
    void modificaProducto() {
        Producto productoExistente = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        Producto productoActualizado = new Producto(1, "Torta cuadrada","Torta Chocolate" , 35000, "sin-gluten", "Torta sabrosa", "chocolate.jpg");
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        Producto result = productoService.modificaProducto(productoActualizado);

        assertNotNull(result);
        assertEquals(productoActualizado.getCode(), result.getCode());
        assertEquals("Bolsa A Modificada", result.getName());
        assertEquals(600, result.getPrice());
        verify(productoRepository, times(1)).save(productoActualizado);
    }

    @Test
    void eliminarProducto() {
        int id = 1;
        doNothing().when(productoRepository).deleteById(id);

        productoService.eliminarProducto(id);

        verify(productoRepository, times(1)).deleteById(id);
    }
}
