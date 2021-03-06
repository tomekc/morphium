package de.caluga.test.mongo.suite;

import de.caluga.morphium.MorphiumSingleton;
import de.caluga.morphium.Query;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Stephan Bösebeck
 * Date: 11.05.12
 * Time: 23:18
 * <p/>
 * TODO: Add documentation here
 */
public class SortingTest extends MongoTest {
    private void prepare() {
        for (int i = 0; i < 5000; i++) {
            UncachedObject uc = new UncachedObject();
            uc.setValue("Random value");
            uc.setCounter((int) (Math.random() * 6000));
            MorphiumSingleton.get().store(uc);
        }
        UncachedObject uc = new UncachedObject();
        uc.setValue("Random value");
        uc.setCounter(-1);
        MorphiumSingleton.get().store(uc);
        uc = new UncachedObject();
        uc.setValue("Random value");
        uc.setCounter(7599);
        MorphiumSingleton.get().store(uc);
    }

    @Test
    public void sortTestDescending() throws Exception {
        prepare();

        Query<UncachedObject> q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        q = q.sort("-counter");

        List<UncachedObject> lst = q.asList();
        int lastValue = 8888;

        for (UncachedObject u : lst) {
            assert (lastValue >= u.getCounter()) : "Counter not smaller, last: " + lastValue + " now:" + u.getCounter();
            lastValue = u.getCounter();
        }
        assert (lastValue == -1) : "Last value wrong: " + lastValue;


        q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        Map<String, Integer> order = new HashMap<String, Integer>();
        order.put("counter", -1);
        q = q.sort(order);

        lst = q.asList();
        lastValue = 8888;

        for (UncachedObject u : lst) {
            assert (lastValue >= u.getCounter()) : "Counter not smaller, last: " + lastValue + " now:" + u.getCounter();
            lastValue = u.getCounter();
        }
        assert (lastValue == -1) : "Last value wrong: " + lastValue;

    }


    @Test
    public void sortTestAscending() throws Exception {
        prepare();

        Query<UncachedObject> q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        q = q.sort("counter");

        List<UncachedObject> lst = q.asList();
        int lastValue = -1;

        for (UncachedObject u : lst) {
            assert (lastValue <= u.getCounter()) : "Counter not greater, last: " + lastValue + " now:" + u.getCounter();
            lastValue = u.getCounter();
        }
        assert (lastValue == 7599) : "Last value wrong: " + lastValue;


        q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        Map<String, Integer> order = new HashMap<String, Integer>();
        order.put("counter", 1);
        q = q.sort(order);

        lst = q.asList();
        lastValue = -1;

        for (UncachedObject u : lst) {
            assert (lastValue <= u.getCounter()) : "Counter not smaller, last: " + lastValue + " now:" + u.getCounter();
            lastValue = u.getCounter();
        }
        assert (lastValue == 7599) : "Last value wrong: " + lastValue;

    }


    @Test
    public void sortTestLimit() throws Exception {
        prepare();

        Query<UncachedObject> q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        q = q.sort("counter");
        q.limit(1);
        List<UncachedObject> lst = q.asList();
        assert (lst.size() == 1) : "List sizer wrong: " + lst.size();
        assert (lst.get(0).getCounter() == -1) : "Smalest value wrong, should be -1, is " + lst.get(0).getCounter();

        q = MorphiumSingleton.get().createQueryFor(UncachedObject.class);
        q = q.f("value").eq("Random value");
        q = q.sort("-counter");
        UncachedObject uc = q.get();
        assert (uc != null) : "not found?!?";
        assert (uc.getCounter() == 7599) : "Highest value wrong, should be 7599, is " + uc.getCounter();

    }

}
