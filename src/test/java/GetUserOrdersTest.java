import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.Ingredients;
import praktikum.models.Order;
import praktikum.order.OrderClient;
import praktikum.order.OrderHelper;
import praktikum.user.UserClient;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static praktikum.utils.JsonParser.getObjectsList;

public class GetUserOrdersTest {

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
    @DisplayName("get orders from authorized user")
    public void getOrdersAuthorized() {
        String token = orderHelper.authorize();
        List<String> ingredients = orderHelper.preparingIngredientsHashForOrder();
        Ingredients ingredientsList = new Ingredients(ingredients);
        Response createOrderResponse = orderClient.createOrder(token, ingredientsList);
        String orderId = createOrderResponse.path("order._id");
        Response getUsersOrderListResponse = orderClient.getUserOrdersList(token);
        getUsersOrderListResponse.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        List<Order> ordersList = getObjectsList(getUsersOrderListResponse.asString(), Order[].class, "$.orders");
        boolean result = false;
        for (Order order : ordersList) {
            if (order.getId().equals(orderId)) {
                result = true;
                break;
            }
        }
        Assert.assertTrue("Заказа с id " + orderId + " не найдено", result);
        userClient.delete(token);
    }

    @Test
    @DisplayName("get orders without authorization")
    public void getOrdersNoNAuthorized() {
        Response getUsersOrderListResponse = orderClient.getUserOrdersList("");
        getUsersOrderListResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}
