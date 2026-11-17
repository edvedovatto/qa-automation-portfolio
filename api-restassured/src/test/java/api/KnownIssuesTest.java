package api;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("known-issue")
class KnownIssuesTest extends BaseTest {

    // Bug: the app accepts a signup with an invalid email and returns 201
    @Test
    void signupWithAnInvalidEmailReturns400() {
        Credentials invalid = new Credentials("notanemail" + System.nanoTime(), "Password123");

        shop.createUser(invalid).then().statusCode(400);
    }

    // Bug: the app accepts a negative quantity and returns 200
    @Test
    void negativeQuantityReturns400() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), -1))
                .then()
                .statusCode(400);
    }

    // Bug: the app lets a user read the basket of another user and returns 200
    @Test
    void basketOfAnotherUserIsRefused() {
        LoginResponse.Authentication session = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();

        shop.basket(session.token(), other.bid())
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(403)));
    }

    // Bug: the app answers 500 when the order history is requested without login
    @Test
    void orderHistoryRequiresLogin() {
        shop.orderHistory().then().statusCode(401);
    }

    // Bug: the app accepts a signup without a password and returns 201
    @Test
    void signupWithoutPasswordReturns400() {
        shop.createUser(Map.of("email", randomCredentials().email())).then().statusCode(400);
    }

    // Bug: the app accepts a signup with different passwords and returns 201
    @Test
    void signupWithDifferentPasswordsReturns400() {
        NewUser different = new NewUser(randomCredentials().email(), "Password123", "Other123");

        shop.createUser(different).then().statusCode(400);
    }

    // Bug: the app accepts quantity zero and returns 200
    @Test
    void quantityZeroReturns400() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 0))
                .then()
                .statusCode(400);
    }

    // Bug: the app answers 500 when the login body is not valid json
    @Test
    void loginWithMalformedJsonReturns400() {
        shop.login("{\"email\":").then().statusCode(400);
    }

    // Bug: the app answers 500 when the product does not exist
    @Test
    void unknownProductInTheBasketIsRefused() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(99999, session.bid(), 1))
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));
    }

    // Bug: the app accepts an address with only the name and returns 201
    @Test
    void addressWithMissingFieldsReturns400() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addAddress(session.token(), Map.of("fullName", "Joao Silva")).then().statusCode(400);
    }

    // Bug: the app lets a user change an item in the basket of another user and returns 200
    @Test
    void changingAnItemOfAnotherUserIsRefused() {
        LoginResponse.Authentication session = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();
        int itemId =
                shop.addToBasket(other.token(), new BasketItem(APPLE_JUICE_ID, other.bid(), 1))
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("data.id");

        shop.updateBasketItem(session.token(), itemId, 3)
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(403)));
    }

    // Bug: the app lets a user check out the basket of another user and returns 200
    @Test
    void checkoutOfAnotherUserBasketIsRefused() {
        LoginResponse.Authentication session = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();
        shop.addToBasket(other.token(), new BasketItem(APPLE_JUICE_ID, other.bid(), 1))
                .then()
                .statusCode(200);

        shop.checkout(session.token(), other.bid())
                .then()
                .statusCode(anyOf(equalTo(401), equalTo(403)));
    }

    // Bug: the app accepts a checkout with an empty basket and returns 200
    @Test
    void checkoutWithAnEmptyBasketReturns400() {
        LoginResponse.Authentication session = loggedInUser();

        shop.checkout(session.token(), session.bid()).then().statusCode(400);
    }
}
