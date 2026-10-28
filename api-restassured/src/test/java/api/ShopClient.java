package api;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

class ShopClient {

    private final RequestSpecification spec;

    ShopClient(RequestSpecification spec) {
        this.spec = spec;
    }

    Response createUser(Credentials user) {
        NewUser body = new NewUser(user.email(), user.password(), user.password());
        return given().spec(spec).body(body).post("/api/Users");
    }

    Response login(Credentials user) {
        return given().spec(spec).body(user).post("/rest/user/login");
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
        return given().spec(spec).header("Authorization", "Bearer " + token)
                .get("/rest/basket/{id}", basketId);
    }

    Response addToBasket(String token, BasketItem item) {
        return given().spec(spec).header("Authorization", "Bearer " + token)
                .body(item).post("/api/BasketItems");
    }
}
