package com.ironhack.edgeproductservice.service.interfaces;

import com.ironhack.edgeproductservice.classes.StoreProduct;

import java.util.Currency;

public interface IEdgeProductService {

    StoreProduct productInOtherCurrency(Integer productId, String currency);
}
