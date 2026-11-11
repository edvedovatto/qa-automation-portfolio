package api;

import com.fasterxml.jackson.annotation.JsonProperty;

record BasketItem(
        @JsonProperty("ProductId") int productId,
        @JsonProperty("BasketId") int basketId,
        int quantity) {}
