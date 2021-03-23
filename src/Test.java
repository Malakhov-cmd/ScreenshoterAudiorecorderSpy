import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.awt.*;

public class Test {
    public static void main(String[] args) {
        
        String ACCESS_TOKEN = "LTM3IAHvrWAAAAAAAAAAEIfvxRgK1IMW8o2fMZxKJF72g4y6NJWKG0hYqlTzzeRd";

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        MyThread task = new MyThread(client);
        task.letsStart();
    }

}

