import com.ershi.mainapp.MainAppApplication;
import com.ershi.user.service.IUserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {MainAppApplication.class,}
)
public class UserTest {

    @Resource
    private IUserService userService;

    @Test
    public void sendEmailCaptcha() {
        userService.sendEmailCaptcha("2402554312@qq.com");
    }
}
