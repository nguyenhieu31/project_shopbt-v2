package com.shopproject.shopbt.controller;

import com.shopproject.shopbt.dto.CategoriesDTO;
import com.shopproject.shopbt.dto.ProductsDTO;
import com.shopproject.shopbt.request.OffsetBasedPageRequest;
import com.shopproject.shopbt.service.catrgory.CategoryService;
import com.shopproject.shopbt.service.product.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/web")
public class ShopController {
    private CategoryService categoryService;
    private ProductService productService;
    @GetMapping("/category")
    public ResponseEntity<?> findAllCategory(){
        try{
            Set<CategoriesDTO> categoriesDTOS= categoryService.findAllCategory();
            return ResponseEntity.status(200).body(categoriesDTOS);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("category/{id}")
    public ResponseEntity<?> findProductsByCategoryId(@PathVariable("id") Long id){
        try{
            Set<ProductsDTO> productsDTOS= productService.findProductsByCategoryId(id);
            return ResponseEntity.status(200).body(productsDTOS);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/shop")
    public ResponseEntity<?> findProductShop(@RequestParam int offset, @RequestParam int limit){
        try{
            Pageable pageable= new OffsetBasedPageRequest(offset,limit, Sort.Direction.ASC,"productId");
            Set<ProductsDTO> products= productService.findALLByLimitOffset(pageable);
            return ResponseEntity.status(200).body(products);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/shop/products")
    public ResponseEntity<?> findProductByCategoryId(@RequestParam("categoryId") Long categoryId){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(productService.findProductsByCategoryId(categoryId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/products/search")
    public ResponseEntity<?> findProductBySearch(@RequestParam("searchName") String searchName){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(productService.findByNameLikeIgnoreCase(searchName));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/shop/products/sort")
    public ResponseEntity<?> findALlProductBySortPrice(@RequestParam("sortName") String sortName,@RequestParam int offset, @RequestParam int limit){
        try{
            Pageable pageable=null;
            if(sortName.equals("increase")){
                pageable=new OffsetBasedPageRequest(offset,limit,Sort.Direction.ASC,"price");
            }else{
                pageable= new OffsetBasedPageRequest(offset,limit,Sort.Direction.DESC,"price");
            }
            Set<ProductsDTO> products= productService.findALLByLimitOffset(pageable);
            products=sortName.equals("increase")?products.stream().sorted(Comparator.comparing(ProductsDTO::getPrice)).collect(Collectors.toCollection(LinkedHashSet::new)):
                    products.stream().sorted(Comparator.comparing(ProductsDTO::getPrice).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
            List<ProductsDTO> productsDTOS= new ArrayList<>();
            for(ProductsDTO product:products){
                productsDTOS.add(product);
            }
            return ResponseEntity.status(HttpStatus.OK).body(productsDTOS);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

}
