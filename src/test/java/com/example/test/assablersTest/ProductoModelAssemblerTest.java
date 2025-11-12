package com.example.test.assablersTest;

import com.example.test.assemblers.ProductoModelAssembler;
import com.example.test.model.Producto;   
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;


public class ProductoModelAssemblerTest {

    private ProductoModelAssembler assembler;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assembler = new ProductoModelAssembler();
    }

    @Test
    void toModelCreatesCorrectLinksForProducto() {
        Producto producto = new Producto();
        producto.setCode(401);

        EntityModel<Producto> entityModel = assembler.toModel(producto);

        assertNotNull(entityModel, "The EntityModel should not be null");
        assertEquals(producto, entityModel.getContent(), "The EntityModel should contain the original Producto object");

        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink, "The EntityModel should have a 'self' link");
        assertTrue(selfLink.getHref().contains("/producto/" + producto.getCode()), "The 'self' link should point to the producto by its ID");
        assertEquals("self", selfLink.getRel().value(), "The relation of the 'self' link should be 'self'");

        Link allProductosLink = entityModel.getLink("productos").orElse(null);
        assertNotNull(allProductosLink, "The EntityModel should have a 'productos' link");
        assertTrue(allProductosLink.getHref().contains("/producto"), "The 'productos' link should point to the list of all productos");
        assertEquals("productos", allProductosLink.getRel().value(), "The relation of the 'productos' link should be 'productos'");
    }

    @Test
    void toModelHandlesNullProductoGracefully() {
        Producto producto = null;
        EntityModel<Producto> entityModel = assembler.toModel(producto);

        assertNotNull(entityModel, "The EntityModel should not be null even if the content is null");
        assertNotNull(entityModel.getContent(), "The content of the EntityModel should not be null");
        assertEquals(0, entityModel.getContent().getCode(), "The Producto ID should be the default value (0)");
        assertTrue(entityModel.getLinks().isEmpty(), "No links should be generated for a null producto input");
    }
}