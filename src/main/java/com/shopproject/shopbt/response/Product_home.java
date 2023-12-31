package com.shopproject.shopbt.response;

import com.shopproject.shopbt.dto.ProductsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product_home {
    private  Set<ProductsDTO> products_new;
    private  Set<ProductsDTO> products_selling;
    private Set<ProductsDTO> products_featured;
}
