public class UseMeter {

    public static void main(String[] args) {

        Meter meter1 = new Meter(3.0d);
        Meter meter2 = meter1 + meter1;
        System.out.println(meter2);

    }
}
