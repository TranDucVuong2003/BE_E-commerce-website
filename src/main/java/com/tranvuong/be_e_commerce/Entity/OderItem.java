package com.tranvuong.be_e_commerce.Entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OderItem {
    @Id
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Oder oder;

    Product product;
    Double quantity;
    Double total_price;
    LocalDate created_at;
}
