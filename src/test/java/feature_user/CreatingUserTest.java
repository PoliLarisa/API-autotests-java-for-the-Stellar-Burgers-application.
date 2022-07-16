package feature_user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import users.User;
import users.UserClient;

import static org.junit.Assert.*;

@Epic("Creating new user role")
@Feature("Registration of user")
public class CreatingUserTest {

    private UserClient userClient;
    private String auth;
    private User user;

    @Before
    public void setup() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (auth != null)
            userClient.deletingUser(auth);
       }


    @Test
    @DisplayName("Creating user")
    @Description("Basic test user is created")
    public void creatingUserTest() {
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);
        auth = response.extract().path("accessToken").toString().substring(7);

        boolean isCreated = response.extract().path("success");
        int statusCode = response.extract().statusCode();
        assertTrue("User not be created", isCreated);
        assertEquals("Incorrect status code", 200, statusCode);
    }

  
    @Test
    @DisplayName("Creating second user")
    @Description("Test for can't be created second user")
    public void secondUserTest() {
        user = User.getRandom();
        userClient.userCreate(user);

        ValidatableResponse secondUser = userClient.userCreate(user);

        boolean isNotCreated = secondUser.extract().path("success");
        String message = secondUser.extract().path("message");
        int statusCode = secondUser.extract().statusCode();
        if (secondUser.extract().body().path("accessToken") != null) {
            String auth = secondUser.extract().body().path("accessToken");
            userClient.deletingUser(auth);
        }
        assertFalse("Second user was created",isNotCreated);
        assertEquals("Error message doesn't match","User already exists", message);
        assertEquals("Incorrect status code",403, statusCode);
    }

    @Test
    @DisplayName("Creating user without credentials {email}")
    @Description("Test for can't be created user without email")
    public void creatingWithoutEmailTest() {
        user = User.builder()
                .password(RandomStringUtils.randomAlphabetic(10))
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        if (response.extract().body().path("accessToken") != null) {
            String auth = response.extract().body().path("accessToken");
            userClient.deletingUser(auth);
        }
        assertFalse("User be created", isNotCreated);
        assertEquals("Error message doesn't match","Email, password and name are required fields", message);
        assertEquals("Incorrect status code",403, statusCode);
    }

    @Test
    @DisplayName("Creating user without credentials {password}")
    @Description("Test for can't be created user without password")
    public void creatingWithoutPasswordTest() {
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(10) + "@testdata.com")
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        if (response.extract().body().path("accessToken") != null) {
            String auth = response.extract().body().path("accessToken");
            userClient.deletingUser(auth);
        }
        assertFalse("User be created", isNotCreated);
        assertEquals("Error message doesn't match","Email, password and name are required fields", message);
        assertEquals("Incorrect status code",403, statusCode);
    }

    @Test
    @DisplayName("Creating user without credentials {name}")
    @Description("Test for can't be created user without name")
    public void creatingWithoutNameTest() {
        user = User.builder()
                .email(RandomStringUtils.randomAlphabetic(10) + "@testdata.com")
                .password(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.userCreate(user);

        boolean isNotCreated = response.extract().path("success");
        String message = response.extract().path("message");
        int statusCode = response.extract().statusCode();
        if (response.extract().body().path("accessToken") != null) {
            String auth = response.extract().body().path("accessToken");
            userClient.deletingUser(auth);
        }
        assertFalse("User be created",isNotCreated);
        assertEquals("Error message doesn't match","Email, password and name are required fields", message);
        assertEquals("Incorrect status code",403, statusCode);
    }
}