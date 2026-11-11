package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AccountDataTest extends BaseTest {

    @Test
    void savedAddressIsListed() {
        LoginResponse.Authentication session = loggedInUser();
        addAddress(session);

        shop.addresses(session.token())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/address-list.json"))
                .body("data.fullName", hasItem("Joao Silva"));
    }

    @Test
    void cardNumberIsMaskedInTheList() {
        LoginResponse.Authentication session = loggedInUser();
        Card card = new Card("Joao Silva", 4111111111111111L, 12, 2090);

        shop.addCard(session.token(), card).then().statusCode(201);

        shop.cards(session.token())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/card-list.json"))
                .body("data.cardNum", hasItem("************1111"));
    }

    @Test
    void addressesRequireLogin() {
        shop.addresses().then().statusCode(401);
    }

    @Test
    void cardsRequireLogin() {
        shop.cards().then().statusCode(401);
    }

    @ParameterizedTest(name = "card {0}, month {1}, year {2}")
    @CsvSource({"123, 12, 2090", "4111111111111111, 13, 2090", "4111111111111111, 12, 2000"})
    void invalidCardIsRefused(long number, int month, int year) {
        LoginResponse.Authentication session = loggedInUser();

        shop.addCard(session.token(), new Card("Joao Silva", number, month, year))
                .then()
                .statusCode(400);

        shop.cards(session.token()).then().body("data", empty());
    }

    @Test
    void addressOfAnotherUserCannotBeRead() {
        LoginResponse.Authentication owner = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();
        int addressId = addAddress(owner);

        shop.address(other.token(), addressId)
                .then()
                .statusCode(400)
                .body(not(containsString("Joao Silva")));
    }

    @Test
    void addressOfAnotherUserCannotBeDeleted() {
        LoginResponse.Authentication owner = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();
        int addressId = addAddress(owner);

        shop.deleteAddress(other.token(), addressId).then().statusCode(400);

        shop.addresses(owner.token()).then().body("data.id", hasItem(addressId));
    }

    @Test
    void cardOfAnotherUserCannotBeRead() {
        LoginResponse.Authentication owner = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();
        int cardId =
                shop.addCard(owner.token(), new Card("Joao Silva", 4111111111111111L, 12, 2090))
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("data.id");

        shop.card(other.token(), cardId)
                .then()
                .statusCode(400)
                .body(not(containsString("Joao Silva")));
    }

    private int addAddress(LoginResponse.Authentication session) {
        Address address =
                new Address(
                        "Joao Silva",
                        1199998888,
                        "01001000",
                        "Rua A 10",
                        "Sao Paulo",
                        "SP",
                        "Brasil");
        return shop.addAddress(session.token(), address)
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");
    }
}
