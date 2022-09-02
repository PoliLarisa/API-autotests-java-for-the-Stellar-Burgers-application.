package orders;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.List;

@Data
public class Order {

    private List<String> ingredients;

    public static String getRandomHashes(){
        return RandomStringUtils.randomAlphabetic(24);
    }

}