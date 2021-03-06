package de.caluga.test.mongo.suite;

import de.caluga.morphium.MorphiumSingleton;
import de.caluga.morphium.annotations.Entity;
import de.caluga.morphium.annotations.Id;
import de.caluga.morphium.annotations.caching.NoCache;
import org.bson.types.ObjectId;
import org.junit.Test;

/**
 * User: Stephan Bösebeck
 * Date: 01.06.12
 * Time: 15:27
 * <p/>
 * TODO: Add documentation here
 */
public class ArrayTest extends MongoTest {

    @Entity
    @NoCache
    public static class ArrayTestObj {
        @Id
        private ObjectId id;
        private String name;
        private int[] intArr;
        private String[] stringArr;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int[] getIntArr() {
            return intArr;
        }

        public void setIntArr(int[] intArr) {
            this.intArr = intArr;
        }

        public String[] getStringArr() {
            return stringArr;
        }

        public void setStringArr(String[] stringArr) {
            this.stringArr = stringArr;
        }
    }

    @Test
    public void testArrays() throws Exception {
        MorphiumSingleton.get().clearCollection(ArrayTestObj.class);
        Thread.sleep(500);
        ArrayTestObj obj = new ArrayTestObj();
        obj.setName("Name");
        obj.setIntArr(new int[]{1, 5, 3, 2});
        obj.setStringArr(new String[]{"test", "string", "array"});
        MorphiumSingleton.get().store(obj);

        obj = MorphiumSingleton.get().readAll(ArrayTestObj.class).get(0);
        assert (obj.getIntArr() != null && obj.getIntArr().length != 0) : "No ints found";
        assert (obj.getStringArr() != null && obj.getStringArr().length > 0) : "No strings found";
    }
}
