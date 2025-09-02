import com.ershi.hotboard.datasource.BiliBiliDataSource;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import org.junit.jupiter.api.Test;

public class DataSourceTest {

    @Test
    public void getHotBoardData() {
        HotBoardEntity hotBoardData = new BiliBiliDataSource().getHotBoardData();
    }
}
