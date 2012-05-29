package de.caluga.test.mongo.suite;

import de.caluga.morphium.annotations.Entity;
import de.caluga.morphium.annotations.Id;
import de.caluga.morphium.annotations.Reference;
import de.caluga.morphium.annotations.caching.NoCache;
import org.bson.types.ObjectId;

/**
 * User: Stephan Bösebeck
 * Date: 29.05.12
 * Time: 00:03
 * <p/>
 * TODO: Add documentation here
 */
@Entity
@NoCache
public class LazyLoadingObject {
    @Id
    private ObjectId id;

    @Reference(lazyLoading = true)
    private UncachedObject lazyUncached;

    @Reference(lazyLoading = true)
    private CachedObject lazyCached;


    private String name;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UncachedObject getLazyUncached() {
        return lazyUncached;
    }

    public void setLazyUncached(UncachedObject lazyUncached) {
        this.lazyUncached = lazyUncached;
    }

    public CachedObject getLazyCached() {
        return lazyCached;
    }

    public void setLazyCached(CachedObject lazyCached) {
        this.lazyCached = lazyCached;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}