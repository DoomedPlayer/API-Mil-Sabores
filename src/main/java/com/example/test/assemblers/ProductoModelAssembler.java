package com.example.test.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import com.example.test.model.Producto;
import com.example.test.controller.ProductoControllerV2;


@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>>{

    @Override
    public EntityModel<Producto> toModel(@NonNull Producto producto){
        if(producto == null){
            return EntityModel.of(new Producto());
        }
        return EntityModel.of(producto,
        linkTo(methodOn(ProductoControllerV2.class).getProductoByCodigo(producto.getCode())).withSelfRel(),
        linkTo(methodOn(ProductoControllerV2.class).getAllProductos()).withRel("productos"));
    }
}
