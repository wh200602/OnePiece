package org.example.onepiece.dto;

import lombok.Data;

@Data
public class CartVO {
    private Long id;
    private Long shopId;
    private String productName;
    private String description;
    private Double price;
    private String imageUrl;
    private Integer num;
    private Integer stock;
}
