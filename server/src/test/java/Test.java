public class Test
{
    public static void main(String[] args) {
        Test t = new Test();
        t.test();
    }

    private String formatTimeMs(long pMilliSecond) {
        int hh = (int) (pMilliSecond / 3600_000L);
        int mm = (int) ((pMilliSecond - (hh * 3600_000L)) / 60_000);
        int  sec = (int) ((pMilliSecond - (hh * 3600_000L) - (mm * 60_000)) / 1000L);
        return String.format("%d:%02d:%02d", hh, mm, sec);
    }
    void test() {
        long tStartTime = System.currentTimeMillis();
        while (true) {
            try { Thread.sleep(10000L);}
            catch( InterruptedException e) {}
            System.out.println("Time: " + formatTimeMs( System.currentTimeMillis() - tStartTime));
        }
    }
}
