package api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

abstract class BaseTest {

    protected static final int APPLE_JUICE_ID = 1;
    protected static final String APPLE_JUICE = "Apple Juice (1000ml)";
    protected static final String BANANA_JUICE = "Banana Juice (1000ml)";

    private static final String BASE_URL =
            System.getProperty(
                    "baseUrl", System.getenv().getOrDefault("BASE_URL", "http://localhost:3000"));

    private final RequestSpecification spec =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_URL)
                    .setContentType(ContentType.JSON)
                    .setAccept(ContentType.JSON)
                    .build();

    protected final ShopClient shop = new ShopClient(spec);

    @BeforeAll
    static void logFailures() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected Credentials randomCredentials() {
        return new Credentials("user" + System.nanoTime() + "@example.com", "Password123");
    }

    protected Credentials newUser() {
        Credentials user = randomCredentials();
        shop.createUser(user).then().statusCode(201);
        return user;
    }

    protected LoginResponse.Authentication loggedInUser() {
        return shop.login(newUser())
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class)
                .authentication();
    }
}
