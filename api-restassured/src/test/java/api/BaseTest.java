package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

abstract class BaseTest {

    private static final String BASE_URL = System.getProperty("baseUrl",
            System.getenv().getOrDefault("BASE_URL", "http://localhost:3000"));

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build();

    protected final ShopClient shop = new ShopClient(spec);

    protected Credentials newUser() {
        Credentials user = new Credentials("user" + System.nanoTime() + "@gmail.com", "Password123");
        shop.createUser(user).then().statusCode(201);
        return user;
    }
}
