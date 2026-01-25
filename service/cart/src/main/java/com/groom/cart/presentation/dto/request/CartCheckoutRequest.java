package com.groom.cart.presentation.dto.request;

import java.util.List;

import com.groom.cart.domain.model.CartItemKey;

import lombok.Getter;

@Getter
public class CartCheckoutRequest {
    private List<CartItemKey> selectedItems;
}
