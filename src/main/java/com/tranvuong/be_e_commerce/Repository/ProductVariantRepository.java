package com.tranvuong.be_e_commerce.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tranvuong.be_e_commerce.Entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
}