package com.shopproject.shopbt.service.product;

import com.shopproject.shopbt.dto.ProductsDTO;
import com.shopproject.shopbt.entity.*;
import com.shopproject.shopbt.repository.category.CategoryRepository;
import com.shopproject.shopbt.repository.color.ColorRepository;
import com.shopproject.shopbt.repository.product.ProductRepository;
import com.shopproject.shopbt.repository.size.SizeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ColorRepository colorRepository;
    private SizeRepository sizeRepository;
    private ModelMapper modelMapper;

    @Override
    public void create_Product(ProductsDTO productsDTO) {
        Product product = new Product();
        product = readProductDTO(product, productsDTO);
        productRepository.save(product);
    }


    public ProductsDTO
    findProductById(Long id) {
        try{
            Product product = productRepository.findByProductId(id);
            ProductsDTO productsDTO = readProduct(product, new ProductsDTO());
            return productsDTO;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private ProductsDTO ConvertOneDTO(Object[] product) {
        ProductsDTO productsDTO = new ProductsDTO();
        productsDTO.setProductId((Long) product[0]);
        productsDTO.setName((String) product[1]);
        productsDTO.setColorNames((Set<Color>) product[2]);
        productsDTO.setDescription((String) product[3]);
        productsDTO.setGalleryImages((Set<Gallery_Image>) product[4]);
        productsDTO.setImage((String) product[5]);
        productsDTO.setPrice((BigDecimal) product[6]);
        productsDTO.setMaterial((String) product[7]);
        productsDTO.setSizeNames((List<String>) product[8]);
        productsDTO.setQuantity((Integer) product[9]);

        return productsDTO;
    }
        @Override
        public void update_Product(ProductsDTO productsDTO) {
            Product product = productRepository.findById(productsDTO.getProductId()).get();
            product = readProductDTO(product,productsDTO);

            productRepository.save(product);
        }
        private Product readProductDTO(Product product,ProductsDTO productsDTO){
            product.setCreatedBy(productsDTO.getCreatedBy());
            product.setName(productsDTO.getName());
            product.setDescription(productsDTO.getDescription());
//        product.setImage(productsDTO.getImage());
            product.setPrice(productsDTO.getPrice());
            product.setMaterial(productsDTO.getMaterial());
            product.setQuantity(productsDTO.getQuantity());
            Categories category = categoryRepository.findCategoriesByCategoryId(productsDTO.getCategoryId());
            product.setCategory(category);

//        // add set colors
//        Set<Integer> colorIds = productsDTO.getColorId();
//        Set<Color> colors = new HashSet<>();
//        colorIds.forEach(colorId -> {
//            colors.add(colorRepository.findByColorId(colorId));
//        });
//        product.setColors(colors);
//
//
//        // add set sizes
//        Set<Integer> sizeIds = productsDTO.getSizeId();
//        Set<Size> sizes = new HashSet<>();
//        sizeIds.forEach(sizeId -> {
//            sizes.add(sizeRepository.findBySizeId(sizeId));
//        });
//        product.setSizes(sizes);
            product.setUpdatedBy(productsDTO.getUpdatedBy());
            return product;
        }

        private ProductsDTO readProduct(Product product,ProductsDTO productsDTO){
            productsDTO.setProductId(product.getProductId());
            productsDTO.setCreatedBy(product.getCreatedBy());
            productsDTO.setCreatedAt(product.getCreatedAt());
            productsDTO.setName(product.getName());
            productsDTO.setCategoryId(product.getCategory().getCategoryId());
            productsDTO.setImage(product.getImage());
            productsDTO.setDescription(product.getDescription());
            productsDTO.setPrice(product.getPrice());
            productsDTO.setPriceDiscount(product.getPriceDiscount());
            productsDTO.setQuantity(product.getQuantity());
            productsDTO.setMaterial(product.getMaterial());
            productsDTO.setColorNames(product.getColors());
            Set<Size> sizes = product.getSizes().stream()
                    .sorted(Comparator.comparing(Size::getSizeId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> sizeNames = new ArrayList<>();
            for (var size : sizes){
                sizeNames.add(size.getName());
            }
            productsDTO.setSizeNames(sizeNames);
            productsDTO.setGalleryImages(product.getGallery_images());

            productsDTO.setUpdatedAt(product.getUpdatedAt());
            productsDTO.setUpdatedBy(product.getUpdatedBy());
            return productsDTO;
    }

    private ProductsDTO ConvertToDto(Object[] product){
        ProductsDTO productsDTO = new ProductsDTO();
        productsDTO.setProductId((Long) product[0]);
        productsDTO.setImage((String) product[1]);
        productsDTO.setName((String) product[2]);
        productsDTO.setPrice((BigDecimal) product[3]);
        return  productsDTO;
        }
    @Override
    public void delete_Product(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Set<ProductsDTO> findALLByLimitOffset(Pageable pageable) {
        try{
            Page<Object[]> products = productRepository.findDataByLimitOffset(pageable);
            Set<ProductsDTO> productsDTOS = products.stream()
                    .map(this::ConvertToDto)
                    .collect(Collectors.toSet());
            return productsDTOS;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Set<ProductsDTO> findProductsByCategoryId(Long id) {
        try{
            Set<Object[]> products = productRepository.findProductByCateId(id);
            Set<ProductsDTO> productsDTOS = products.stream()
                    .map(this::ConvertToDto)
                    .collect(Collectors.toSet());
            return productsDTOS;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Set<ProductsDTO> findByPriceBetweenPrice(BigDecimal startPrice, BigDecimal endPrice) {
        try{
            Set<Object[]> products = productRepository.findByPriceBetweenPrice(startPrice,endPrice);
            Set<ProductsDTO> productsDTOS = products.stream()
                    .limit(4)
                    .map(this::ConvertToDto)
                    .collect(Collectors.toSet());
            return productsDTOS;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Set<ProductsDTO> findTop10ByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        Set<Product> products = productRepository.findTop10ByCreatedAtBetween(startDate,endDate);
        Set<ProductsDTO> productsDTOS = new HashSet<>();
        products.forEach(product -> {
            ProductsDTO productsDTO = new ProductsDTO();
            productsDTO = readProduct(product, productsDTO);
            productsDTOS.add(productsDTO);
        });
        return productsDTOS;
    }

    @Override
    public Set<ProductsDTO> findProductFeature() {
        try{
            Set<Object[]> products = productRepository.findProductFeature();
            Set<ProductsDTO> productsDTOS = products.stream()
                    .limit(4)
                    .map(this::ConvertToDto)
                    .collect(Collectors.toSet());
            return productsDTOS;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Set<ProductsDTO> findByNameLikeIgnoreCase(String name) {
        try{
            Set<Object[]> products = productRepository.findProductByNameLike(name);
            Set<ProductsDTO> productsDTOS = products.stream()
                    .map(this::ConvertToDto)
                    .collect(Collectors.toSet());
            return productsDTOS;
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getFirstTwoWordsFromProductName(String productName) {
        if (productName == null) {
            return null;
        }
        // Chia chuỗi thành các từ
        String[] words = productName.split("\\s+");

        if (words.length >= 2) {
            // Lấy hai từ đầu tiên và kết hợp chúng thành một chuỗi
            return words[0] + " " + words[1];
        } else {
            // Trường hợp có ít hơn hai từ, trả lại chuỗi ban đầu
            return productName;
        }
    }

}
