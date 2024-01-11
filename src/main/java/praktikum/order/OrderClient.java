package praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.models.Ingredients;

import static io.restassured.RestAssured.given;


public class OrderClient {

    private static final String INGREDIENTS_URL = "api/ingredients";
    private static final String USER_ORDERS_URL = "api/orders";
    private static final String ALL_ORDERS_URL = "api/orders/all";

    @Step("Get all actual ingredients")
    public Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(INGREDIENTS_URL);
    }

    @Step("creating order with ingredients and with authorized user")
    public Response createOrder(String token, Ingredients ingredients) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .and()
                .body(ingredients)
                .when()
                .post(USER_ORDERS_URL);
    }

    @Step("getting user orders list")
    public Response getUserOrdersList(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .get(USER_ORDERS_URL);
    }

    @Step("get orders of all users")
    public Response getAllOrders() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(ALL_ORDERS_URL);
    }
}
