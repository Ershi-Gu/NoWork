import com.ershi.hotboard.datasource.JueJinDataSource;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.job.HotBoardAsyncJob;
import com.ershi.mainapp.MainAppApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = {MainAppApplication.class,}
)
public class DataSourceTest {

    @Resource
    private HotBoardAsyncJob hotBoardAsyncJob;

    @Test
    public void getHotBoardData() {
//        HotBoardEntity hotBoardData = new BiliBiliDataSource().getHotBoardData();
        HotBoardEntity hotBoardData = new JueJinDataSource().getHotBoardData();
    }

    @Test
    public void runHBJob() throws InterruptedException {
        hotBoardAsyncJob.run();
        Thread.sleep(5000);
    }
}
