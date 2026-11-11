package api;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

class ShopClient {

    private final RequestSpecification spec;

    ShopClient(RequestSpecification spec) {
        this.spec = spec;
    }

    private RequestSpecification asUser(String token) {
        return given().spec(spec).header("Authorization", "Bearer " + token);
    }

    Response createUser(Credentials user) {
        NewUser body = new NewUser(user.email(), user.password(), user.password());
        return given().spec(spec).body(body).post("/api/Users");
    }

    Response createUser(Object user) {
        return given().spec(spec).body(user).post("/api/Users");
    }

    Response login(Credentials user) {
        return given().spec(spec).body(user).post("/rest/user/login");
    }

    Response login(String rawBody) {
        return given().spec(spec).body(rawBody).post("/rest/user/login");
    }

    Response whoAmI(String token) {
        return given().spec(spec).cookie("token", token).get("/rest/user/whoami");
    }

    Response products() {
        return given().spec(spec).get("/api/Products");
    }

    Response product(int id) {
        return given().spec(spec).get("/api/Products/{id}", id);
    }

    Response search(String term) {
        return given().spec(spec).queryParam("q", term).get("/rest/products/search");
    }

    Response basket(int basketId) {
        return given().spec(spec).get("/rest/basket/{id}", basketId);
    }

    Response basket(String token, int basketId) {
        return asUser(token).get("/rest/basket/{id}", basketId);
    }

    Response addToBasket(BasketItem item) {
        return given().spec(spec).body(item).post("/api/BasketItems");
    }

    Response addToBasket(String token, BasketItem item) {
        return asUser(token).body(item).post("/api/BasketItems");
    }

    Response updateBasketItem(String token, int itemId, int quantity) {
        return asUser(token)
                .body(Map.of("quantity", quantity))
                .put("/api/BasketItems/{id}", itemId);
    }

    Response removeBasketItem(String token, int itemId) {
        return asUser(token).delete("/api/BasketItems/{id}", itemId);
    }

    Response checkout(int basketId) {
        return given().spec(spec).post("/rest/basket/{id}/checkout", basketId);
    }

    Response checkout(String token, int basketId) {
        return asUser(token).post("/rest/basket/{id}/checkout", basketId);
    }

    Response orderHistory() {
        return given().spec(spec).get("/rest/order-history");
    }

    Response orderHistory(String token) {
        return asUser(token).get("/rest/order-history");
    }

    Response addAddress(String token, Object address) {
        return asUser(token).body(address).post("/api/Addresss");
    }

    Response address(String token, int id) {
        return asUser(token).get("/api/Addresss/{id}", id);
    }

    Response deleteAddress(String token, int id) {
        return asUser(token).delete("/api/Addresss/{id}", id);
    }

    Response addresses() {
        return given().spec(spec).get("/api/Addresss");
    }

    Response addresses(String token) {
        return asUser(token).get("/api/Addresss");
    }

    Response addCard(String token, Card card) {
        return asUser(token).body(card).post("/api/Cards");
    }

    Response card(String token, int id) {
        return asUser(token).get("/api/Cards/{id}", id);
    }

    Response cards() {
        return given().spec(spec).get("/api/Cards");
    }

    Response cards(String token) {
        return asUser(token).get("/api/Cards");
    }
}
