package com.ironhack.edgeproductservice.service.impl;
import com.ironhack.edgeproductservice.classes.Price;
import com.ironhack.edgeproductservice.classes.StoreProduct;
import com.ironhack.edgeproductservice.client.ExchangeClient;
import com.ironhack.edgeproductservice.client.StoreProductClient;
import com.ironhack.edgeproductservice.dto.PriceDto;
import com.ironhack.edgeproductservice.service.interfaces.IEdgeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EdgeProductService implements IEdgeProductService {

    @Autowired
    private ExchangeClient exchangeClient;
    @Autowired
    private StoreProductClient storeProductClient;

    private CircuitBreakerFactory circuitBreakerFactory = new Resilience4JCircuitBreakerFactory();

    public StoreProduct productInOtherCurrency(Integer productId, String currency){

        CircuitBreaker exchangeCircuitBreaker = circuitBreakerFactory.create("exchange-service");
        CircuitBreaker storeCircuitBreaker = circuitBreakerFactory.create("storeProducts-service");

        StoreProduct product = storeCircuitBreaker.run(()-> storeProductClient.getProduct(productId),
                throwable -> getProductFallBack(productId));
        if (product == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "We couldn't find the product you were looking for. Please, try again");
        }

        PriceDto originalPrice = new PriceDto();
        originalPrice.setPriceAmount(product.getPrice());
        originalPrice.setPriceCurrency(product.getCurrency().toString());

        Price convertedPrice = exchangeCircuitBreaker.run(()->exchangeClient.convert(originalPrice, currency),
                throwable -> convertFallBack(originalPrice, currency));

        if (convertedPrice == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "We couldn't convert the price. We are learning basic maths operations, be patient...");
        }

        product.setPrice(convertedPrice.getPriceAmount());
        product.setCurrency(convertedPrice.getPriceCurrency());

        return product;
    }

    private Price convertFallBack(PriceDto originalPrice, String currency) {
        return null;
    }

    private StoreProduct getProductFallBack(Integer productId) {
        return null;
    }

}
