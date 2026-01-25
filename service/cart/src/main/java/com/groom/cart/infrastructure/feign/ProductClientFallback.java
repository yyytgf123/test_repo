package com.groom.cart.infrastructure.feign;

import java.util.List;

import org.springframework.stereotype.Component;

import com.groom.cart.application.dto.ProductCartInfo;
import com.groom.cart.application.dto.StockManagement;
import com.groom.common.presentation.advice.CustomException;
import com.groom.common.presentation.advice.ErrorCode;

@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public List<ProductCartInfo> getProductCartInfos(List<StockManagement> requests) {
        throw new CustomException(ErrorCode.PRODUCT_SERVICE_ERROR);
    }
}
