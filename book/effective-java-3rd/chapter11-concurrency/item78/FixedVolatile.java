import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// Use atomic long in incrementational case
public class FixedVolatile {
    private static final AtomicLong nextSerialNum = new AtomicLong();

    public static long generateSerialNumber() {
        return nextSerialNum.getAndIncrement();
    }

    public static void main(String[] args)
            throws InterruptedException {
        Runnable runnable = () -> System.out.println(generateSerialNumber());
        for (int i = 0; i < 100; ++i) {
            new Thread(runnable).start();
        }

        TimeUnit.MILLISECONDS.sleep(300);

        // prints 100
        System.out.println("Final result : " + nextSerialNum.get());
    }
}
