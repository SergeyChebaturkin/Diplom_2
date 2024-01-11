import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.*;
import praktikum.order.OrderClient;
import praktikum.order.OrderHelper;
import praktikum.user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static praktikum.datafaker.OrderDataFaker.randomHash;
import static praktikum.utils.JsonParser.getObjectsList;

public class CreateOrderTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private OrderClient orderClient;
    private UserClient userClient;
    private OrderHelper orderHelper;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderClient = new OrderClient();
        userClient = new UserClient();
        orderHelper = new OrderHelper(userClient, orderClient);
    }

    @Test
    @DisplayName("create order with authorized user and existing ingredients")
    public void createOrderAuthorizedUser() {
        String token = orderHelper.authorize();
        List<String> ingredients = orderHelper.preparingIngredientsHashForOrder();
        Ingredients ingredientsList = new Ingredients(ingredients);
        Response createOrderResponse = orderClient.createOrder(token, ingredientsList);
        createOrderResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .and()
                .statusCode(SC_OK);
        userClient.delete(token);
    }

    @Test
    @DisplayName("create order with non-authorized user and existing ingredients")
    public void createOrderNonAuthorizedUser() {
        List<String> ingredients = orderHelper.preparingIngredientsHashForOrder();
        Ingredients ingredientsList = new Ingredients(ingredients);
        Response createOrderResponse = orderClient.createOrder("", ingredientsList);
        String orderId = createOrderResponse.path("order._id");
        Assert.assertNull(orderId);
        Response getAllOrdersResponse = orderClient.getAllOrders();
        List<Order> ordersList = getObjectsList(getAllOrdersResponse.asString(), Order[].class, "$.orders");
        int number = createOrderResponse.path("order.number");
        boolean isNumberExistInList = false;
        for (Order order : ordersList) {
            if (order.getNumber() == number) {
                isNumberExistInList = true;
                break;
            }
        }
        Assert.assertFalse("Найден заказ с номером " + number + ", которого быть не должно", isNumberExistInList);
    }

    @Test
    @DisplayName("create order with authorized user and without ingredients")
    public void createOrderWithoutIngredients() {
        String token = orderHelper.authorize();
        List<String> ingredients = new ArrayList<>();
        Ingredients ingredientsList = new Ingredients(ingredients);
        Response createOrderResponse = orderClient.createOrder(token, ingredientsList);
        createOrderResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(SC_BAD_REQUEST);
        userClient.delete(token);
    }

    @Test
    @DisplayName("create order with authorized user and invalid ingredients hash")
    public void createOrderInvalidHash() {
        String token = orderHelper.authorize();
        List<String> ingredients = List.of(randomHash(), randomHash(), randomHash());
        Ingredients ingredientsList = new Ingredients(ingredients);
        Response createOrderResponse = orderClient.createOrder(token, ingredientsList);
        createOrderResponse.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
        userClient.delete(token);
    }
}
