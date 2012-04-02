/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.caluga.test.mongo.suite;

import de.caluga.morphium.Morphium;
import de.caluga.morphium.Query;
import de.caluga.morphium.StatisticKeys;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stephan
 */
public class BasicFunctionalityTest extends MongoTest {
    public static final int NO_OBJECTS = 1000;
    private final static Logger log = Logger.getLogger(BasicFunctionalityTest.class);

    public BasicFunctionalityTest() {
    }

    @Test
    public void orTest() {
        log.info("Storing Uncached objects...");

        long start = System.currentTimeMillis();

        for (int i = 1; i <= NO_OBJECTS; i++) {
            UncachedObject o = new UncachedObject();
            o.setCounter(i);
            o.setValue("Uncached " + i);
            Morphium.get().store(o);
        }

        Query<UncachedObject> q = Morphium.get().createQueryFor(UncachedObject.class);
        q.or(q.q().f("counter").lt(10), q.q().f("value").eq("Uncached 50"));
        log.info("Query string: " + q.toQueryObject().toString());
        List<UncachedObject> lst = q.asList();
        for (UncachedObject o : lst) {
            assert (o.getCounter() < 10 || o.getValue().equals("Uncached 50")) : "Value did not match: " + o.toString();
            System.out.println(o.toString());
        }
        log.info("1st test passed");
        for (int i = 1; i < 120; i++) {
            //Storing some additional test content:
            UncachedObject uc = new UncachedObject();
            uc.setCounter(i);
            uc.setValue("Complex Query Test " + i);
            Morphium.get().store(uc);
        }


    }


    @Test
    public void uncachedSingeTest() {
        log.info("Storing Uncached objects...");

        long start = System.currentTimeMillis();

        for (int i = 1; i <= NO_OBJECTS; i++) {
            UncachedObject o = new UncachedObject();
            o.setCounter(i);
            o.setValue("Uncached " + i);
            Morphium.get().store(o);
        }
        long dur = System.currentTimeMillis() - start;
        log.info("Storing single took " + dur + " ms");
        assert (dur < NO_OBJECTS * 1.5) : "Storing took way too long";

        log.info("Searching for objects");

        checkUncached();
    }

    @Test
    public void uncachedListTest() {
        Morphium.get().clearCollection(UncachedObject.class);
        log.info("Preparing a list...");

        long start = System.currentTimeMillis();
        List<UncachedObject> lst = new ArrayList<UncachedObject>();
        for (int i = 1; i <= NO_OBJECTS; i++) {
            UncachedObject o = new UncachedObject();
            o.setCounter(i);
            o.setValue("Uncached " + i);
            lst.add(o);
        }
        Morphium.get().storeList(lst);
        long dur = System.currentTimeMillis() - start;
        log.info("Storing a list  took " + dur + " ms");
        assert (dur < NO_OBJECTS * 1.5) : "Storing took way too long";

        checkUncached();
    }

    private void checkUncached() {
        long start;
        long dur;
        start = System.currentTimeMillis();
        for (int i = 1; i <= NO_OBJECTS; i++) {
            Query<UncachedObject> q = Morphium.get().createQueryFor(UncachedObject.class);
            q.f("counter").eq(i);
            List<UncachedObject> l = q.asList();
            assert (l != null && l.size() > 0) : "Nothing found!?!?!?!? Value: " + i;
            UncachedObject fnd = l.get(0);
            assert (fnd != null) : "Error finding element with id " + i;
            assert (fnd.getCounter() == i) : "Counter not equal: " + fnd.getCounter() + " vs. " + i;
            assert (fnd.getValue().equals("Uncached " + i)) : "value not equal: " + fnd.getCounter() + "(" + fnd.getValue() + ") vs. " + i;
        }
        dur = System.currentTimeMillis() - start;
        log.info("Searching  took " + dur + " ms");
    }

    private void randomCheck() {
        log.info("Random access to cached objects");
        long start;
        long dur;
        start = System.currentTimeMillis();
        for (int idx = 1; idx <= NO_OBJECTS * 1.5; idx++) {
            int i = (int) (Math.random() * (double) NO_OBJECTS);
            if (i == 0) i = 1;
            Query<CachedObject> q = Morphium.get().createQueryFor(CachedObject.class);
            q.f("counter").eq(i);
            List<CachedObject> l = q.asList();
            assert (l != null && l.size() > 0) : "Nothing found!?!?!?!? " + i;
            CachedObject fnd = l.get(0);
            assert (fnd != null) : "Error finding element with id " + i;
            assert (fnd.getCounter() == i) : "Counter not equal: " + fnd.getCounter() + " vs. " + i;
            assert (fnd.getValue().equals("Cached " + i)) : "value not equal: " + fnd.getCounter() + " vs. " + i;
        }
        dur = System.currentTimeMillis() - start;
        log.info("Searching  took " + dur + " ms");
        log.info("Cache Hits Percentage: " + Morphium.get().getStatistics().get(StatisticKeys.CHITSPERC) + "%");
    }


    @Test
    public void cachedWritingTest() {
        log.info("Starting background writing test - single objects");
        long start = System.currentTimeMillis();
        for (int i = 1; i <= NO_OBJECTS; i++) {
            CachedObject o = new CachedObject();
            o.setCounter(i);
            o.setValue("Cached " + i);
            Morphium.get().store(o);
        }
        long dur = System.currentTimeMillis() - start;
        log.info("Storing (in Cache) single took " + dur + " ms");
        waitForWrites();
        dur = System.currentTimeMillis() - start;
        log.info("Storing took " + dur + " ms overall");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        randomCheck();
    }


    @Test
    public void checkListWriting() {
        List<CachedObject> lst = new ArrayList<CachedObject>();
        try {
            Morphium.get().store(lst);
            Morphium.get().storeInBackground(lst);
        } catch (Exception e) {
            log.info("Got exception, good!");
            return;
        }
        assert (false) : "Exception missing!";
    }

    @Test
    public void checkToStringUniqueness() {
        Query<CachedObject> q = Morphium.get().createQueryFor(CachedObject.class);
        q = q.f("value").eq("Test").f("counter").gt(5);
        String t = q.toString();
        log.info("Tostring: " + q.toString());
        q = Morphium.get().createQueryFor(CachedObject.class);
        q = q.f("counter").gt(5).f("value").eq("Test");
        String s = q.toString();
        if (!s.equals(t)) {
            log.warn("Warning: order is important s=" + s + " and t=" + t);
        }

        q = Morphium.get().createQueryFor(CachedObject.class);
        q = q.f("counter").gt(5).order("counter,-value");
        t = q.toString();
        q = Morphium.get().createQueryFor(CachedObject.class);
        q = q.f("counter").gt(5);
        s = q.toString();
        assert (!t.equals(s)) : "Values should not be equal: s=" + s + " t=" + t;
    }

    @Test
    public void mixedListWritingTest() {
        List<Object> tst = new ArrayList<Object>();
        int cached = 0;
        int uncached = 0;
        for (int i = 0; i < NO_OBJECTS; i++) {
            if (Math.random() < 0.5) {
                cached++;
                CachedObject c = new CachedObject();
                c.setValue("List Test!");
                c.setCounter(11111);
                tst.add(c);
            } else {
                uncached++;
                UncachedObject uc = new UncachedObject();
                uc.setValue("List Test uc");
                uc.setCounter(22222);
                tst.add(uc);
            }
        }
        log.info("Writing " + cached + " Cached and " + uncached + " uncached objects!");

        Morphium.get().storeList(tst);
        waitForWrites();
        //Still waiting - storing lists is not shown in number of write buffer entries
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Query<UncachedObject> qu = Morphium.get().createQueryFor(UncachedObject.class);
        assert (qu.countAll() == uncached) : "Difference in object count for cached objects. Wrote " + uncached + " found: " + qu.countAll();
        Query<CachedObject> q = Morphium.get().createQueryFor(CachedObject.class);
        assert (q.countAll() == cached) : "Difference in object count for cached objects. Wrote " + cached + " found: " + q.countAll();

    }

    private void waitForWrites() {
        while (Morphium.get().writeBufferCount() > 0) {
//            log.info("...");
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(BasicFunctionalityTest.class).fatal(ex);
            }
        }
    }
}
