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

    Response search(String term) {
        return given().spec(spec).queryParam("q", term).get("/rest/products/search");
    }
}
