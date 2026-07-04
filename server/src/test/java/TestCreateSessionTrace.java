import com.hoddmimes.turf.server.generated.MongoAux;
import com.hoddmimes.turf.server.generated.SessionTrace;
import com.hoddmimes.turf.server.generated.TraceZone;
import com.mongodb.client.result.UpdateResult;

import java.text.SimpleDateFormat;
import java.util.Random;

public class TestCreateSessionTrace
{
    static final String[] mUsers = {"Adam","Bertil","Cesar","David", "Erik","Filipp","Gustav","Helge","Ivar","Johan","Kalle","Lasse","Martin","Niklas"};
    static final String[] mRegions = {"Stockholm","Västmaland","Scottland","Skåne","Östergötland"};
    Random mRnd = new Random();
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static void main(String[] args) {
        TestCreateSessionTrace t = new TestCreateSessionTrace();
        t.test();
    }

    TraceZone createTurfZone(int pIndex, long pStartTime) {
        TraceZone tz = new TraceZone();
        tz.setLong( 19.0d + ((double) mRnd.nextInt(6754321 ) / 100000000000d));
        tz.setLat( 59.0d + ((double) mRnd.nextInt(6754321 ) / 100000000000d));
        tz.setEndTime(0L);
        tz.setZoneName("zone-" + (mRnd.nextInt(998) + 1));
        tz.setZoneId(mRnd.nextInt(1234) + 100);
        tz.setTp(65 + mRnd.nextInt(100));
        tz.setPph(1 + mRnd.nextInt(8));
        long take_time =  pStartTime + (pIndex * 120000) + mRnd.nextInt(61234);
        tz.setTakeTime( take_time );
        tz.setTakeTimeStr( SDF.format( take_time ));
        return tz;
    }

    SessionTrace createSessionTrace( int pIndex )  {
        SessionTrace st = new SessionTrace();
        st.setUsername( mUsers[pIndex % mUsers.length]);
        st.setUserDateTime( mUsers[pIndex % mUsers.length] + "-" + SDF.format( System.currentTimeMillis()));
        st.setRegion( mRegions[ (pIndex % mRegions.length)]);
        long start_time = System.currentTimeMillis() + (pIndex *60000);
        st.setStartTime(start_time);
        st.setStartTimeStr( SDF.format( start_time));

        double km = (double) (mRnd.nextInt( 20000) + 10000) /1000.0d;
        int points = mRnd.nextInt(2341) + 5797;
        st.setDistance( km );
        st.setPoints( points );
        st.setLatestTakeTime( System.currentTimeMillis() + (90*60000) + (mRnd.nextInt(120) * 60000));
        st.setSess_pp_km( points / km );
        double hh = ((st.getLatestTakeTime().get() - st.getStartTime().get()) / 3600_000);
        st.setSess_pp_hh( points / hh );

        int zc = 27 + mRnd.nextInt(77 );
        for( int i = 0; i < zc; i++) {
            st.addZones( createTurfZone(i, st.getStartTime().get()));
        }
        return st;
    }


    void test() {
        MongoAux db = new MongoAux("TurfGame", "localhost", 27017 );
        db.connectToDatabase();

        for( int i = 1; i <= 30; i++) {
            SessionTrace sessTrace = createSessionTrace( i );
            UpdateResult tResult = db.updateSessionTrace( sessTrace, true);
            System.out.println("id: " + tResult.getUpsertedId().asObjectId().getValue().toHexString());

        }
    }
}
