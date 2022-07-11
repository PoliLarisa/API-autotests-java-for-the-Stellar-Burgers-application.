package feature5;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import orders.Order;
import orders.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import users.User;
import users.UserClient;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

@Epic("Creating new user role")
@Feature("Get orders")
public class GetOrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private ValidatableResponse response;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
        ValidatableResponse ingredients = orderClient.gettingAllIngredients();
        firstIngredient = ingredients.extract().path("data[1]._id");
    }

    @After
    public void tearDown() {
        userClient.deletingUser(accessToken, user);
    }

    @Test
    @DisplayName("Getting orders")
    @Description("Getting all orders for 1 user with authorization")
    public void getOrderOneUserWithAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));
        orderClient.orderCreateWithAuthorization(accessToken, order);

        response = orderClient.gettingOrderUserWithAuthorization(accessToken);

        int statusCode = response.extract().statusCode();
        boolean isGeted = response.extract().path("success");
        List<String> orders = response.extract().path("orders");
        assertEquals("Incorrect status code", 200, statusCode);
        assertTrue("Orders wasn't get", isGeted);
        assertNotNull("Orders is empty", orders);
    }

    @Test
    @DisplayName("Getting orders")
    @Description("Getting all orders for 1 user without authorization")
    public void getOrderOneUserWithoutAuthorization() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));
        orderClient.orderCreateWithAuthorization(accessToken, order);

        response = orderClient.gettingOrderUserWithoutAuthorization();

        int statusCode = response.extract().statusCode();
        boolean isNotGeted = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Incorrect status code", 401, statusCode);
        assertFalse("Orders wasn't get", isNotGeted);
        assertEquals("Error message not matches", "You should be authorised", message);
    }
}
