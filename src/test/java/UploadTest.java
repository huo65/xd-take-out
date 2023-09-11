import org.junit.jupiter.api.Test;

public class UploadTest {
    @Test
    public void test1() {
        String fileName = "hello.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        System.out.println(suffix);

    }
}
