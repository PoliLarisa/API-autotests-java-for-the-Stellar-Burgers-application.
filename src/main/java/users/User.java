package users;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
public class User {

    private final String email;
    private final String password;
    private final String name;

    @Step("Creation users with random credentials")
    public static User getRandom() {

        final String email = RandomStringUtils.randomAlphabetic(10) + "@testdata.com";
        final String password = RandomStringUtils.randomAlphabetic(10);
        final String name = RandomStringUtils.randomAlphabetic(10);

        Allure.addAttachment("Login", email);
        Allure.addAttachment("Password", password);
        Allure.addAttachment("Name", name);

        return new User(email, password, name);
    }

    public static String getRandomEmail(){

        return RandomStringUtils.randomAlphabetic(10)+"@testdata.com";
    }

    public static String getRandomData(){

        return RandomStringUtils.randomAlphabetic(10);
    }
}