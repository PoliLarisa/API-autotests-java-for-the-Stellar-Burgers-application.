package feature_login;

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
import users.UserCredentials;
import static org.junit.Assert.*;

@Epic("Creating new user role")
@Feature("Validation of user")
public class LoginTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        ValidatableResponse response = userClient.userCreate(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);
    }

    @After
    public void tearDown() {
        if (accessToken != null && user != null)
            userClient.deletingUser(accessToken, user);

    }

    @Test
    @DisplayName("Validation test")
    @Description("Basic validation test with credentials {email and password}")
    public void validationTest() {
        ValidatableResponse response = userClient.validation(UserCredentials.from(user));

        int statusCode = response.extract().statusCode();
        boolean isValidated = response.extract().path("success");
        assertEquals("Incorrect status code",200, statusCode);
        assertTrue("User not validated", isValidated);
    }

    @Test
    @DisplayName("Validation test with incorrect credential {email}")
    @Description("Basic validation test with incorrect email")
    public void validationWithWrongEmailTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(RandomStringUtils.randomAlphabetic(10) + "@testdata.com")
                .password(user.getPassword())
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Incorrect status code",401, statusCode);
        assertFalse("User was validated",isNotValidated);
        assertEquals("Error message doesn't match","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Validation test with incorrect credential {password}")
    @Description("Basic validation test with incorrect password")
    public void validationWithWrongPasswordTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(RandomStringUtils.randomAlphabetic(10))
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Incorrect status code",401, statusCode);
        assertFalse("User was validated",isNotValidated);
        assertEquals("Error message doesn't match","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Validation test without credential {email}")
    @Description("Basic validation test without email")
    public void validationWithoutEmailTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(null)
                .password(user.getPassword())
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Incorrect status code",401, statusCode);
        assertFalse("User was validated",isNotValidated);
        assertEquals("Error message doesn't match","email or password are incorrect", message);
    }

    @Test
    @DisplayName("Validation test without credential {password}")
    @Description("Basic validation test without password")
    public void validationWithoutPasswordTest() {
        UserCredentials credentials = UserCredentials.builder()
                .email(user.getEmail())
                .password(null)
                .build();

        ValidatableResponse response = userClient.validation(credentials);

        int statusCode = response.extract().statusCode();
        boolean isNotValidated = response.extract().path("success");
        String message = response.extract().path("message");
        assertEquals("Incorrect status code",401, statusCode);
        assertFalse("User was validated",isNotValidated);
        assertEquals("Error message doesn't match","email or password are incorrect", message);
    }
}