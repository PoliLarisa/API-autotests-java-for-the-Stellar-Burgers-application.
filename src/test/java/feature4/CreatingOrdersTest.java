package feature4;

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
import static org.junit.Assert.*;

@Epic("Creating new user role")
@Feature("Create order")
public class CreatingOrdersTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private ValidatableResponse response;
    private String accessToken;
    private String firstIngredient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = User.getRandom();
        response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
        ValidatableResponse ingredients = orderClient.gettingAllIngredients();
        firstIngredient = ingredients.extract().path("data[0]._id");
    }

    @After
    public void tearDown() {
        userClient.deletingUser(accessToken, user);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with ingredients and authorization")
    public void creatingOrderWithAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        String name = response.extract().path("name");
        boolean isCreated = response.extract().path("success");
        assertEquals("Incorrect status code", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order doesn't created", isCreated);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with ingredients and without authorization")
    public void creatingOrderWithoutAuthorizationTest() {
        Order order = new Order();
        order.setIngredients(Collections.singletonList(firstIngredient));

        response = orderClient.orderCreateWithoutAuthorization(order);

        int statusCode = response.extract().statusCode();
        String name = response.extract().path("name");
        boolean isCreated = response.extract().path("success");
        assertEquals("Incorrect status code", 200, statusCode);
        assertNotNull("Name is empty", name);
        assertTrue("Order doesn't created", isCreated);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order without ingredients")
    public void creatingOrderWithoutIngredientTest() {
        Order order = new Order();

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        boolean isNotCreated = response.extract().path("success");
        String errorMessage = response.extract().path("message");
        assertEquals("Incorrect status code", 400, statusCode);
        assertFalse("Order be created", isNotCreated);
        assertEquals("Error message doesn't match", "Ingredient ids must be provided", errorMessage);
    }

    @Test
    @DisplayName("Creating orders")
    @Description("Creating order with incorrect hashes ingredients")
    public void creatingOrderWithIncorrectHashesTest() {
        String incorrectHashes = Order.getRandomHashes();
        Order order = new Order();
        order.setIngredients(Collections.singletonList(incorrectHashes));

        response = orderClient.orderCreateWithAuthorization(accessToken, order);

        int statusCode = response.extract().statusCode();
        assertEquals("Incorrect status code", 500, statusCode);
    }
}