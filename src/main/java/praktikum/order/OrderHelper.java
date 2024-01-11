package praktikum.order;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import praktikum.models.Ingredient;
import praktikum.models.User;
import praktikum.user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static praktikum.user.UserGenerator.randomValidUser;
import static praktikum.utils.JsonParser.getObjectsList;

public class OrderHelper {

    private final UserClient userClient;
    private final OrderClient orderClient;

    public OrderHelper(UserClient userClient, OrderClient orderClient) {
        this.userClient = userClient;
        this.orderClient = orderClient;
    }

    @Step
    @DisplayName("authorizing user")
    public String authorize() {
        User user = randomValidUser();
        Response registerResponse = userClient.register(user);
        String token = registerResponse.path("accessToken");
        return token;
    }

    @Step
    @DisplayName("preparing list of ingredients hash for order")
    public List<String> preparingIngredientsHashForOrder() {
        String getIngredientsResponse = orderClient
                .getIngredients()
                .asString();
        List<Ingredient> allIngredients = getObjectsList(getIngredientsResponse, Ingredient[].class, "$.data");
        List<String> ingredientsHashForOrder = new ArrayList<>();
        ingredientsHashForOrder.add(allIngredients.stream().filter(x -> x.getType().equals("bun")).findFirst().get().get_id());
        ingredientsHashForOrder.add(allIngredients.stream().filter(x -> x.getType().equals("main")).findFirst().get().get_id());
        ingredientsHashForOrder.add(allIngredients.stream().filter(x -> x.getType().equals("sauce")).findFirst().get().get_id());
        return ingredientsHashForOrder;
    }
}
