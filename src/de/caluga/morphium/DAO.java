package de.caluga.morphium;

/**
 * User: Stephan Bösebeck
 * Date: 17.05.12
 * Time: 15:06
 * <p/>
 */
public abstract class DAO<T> {
    private Morphium morphium;
    private Class<T> type;

    public DAO(Morphium m, Class<T> type) {
        this.type = type;
        morphium = m;
    }

    public Query<T> getQuery() {
        return morphium.createQueryFor(type);
    }

    public Object getValue(Enum field, T obj) throws IllegalAccessException {
        return getValue(field.name(), obj);
    }

    public Object getValue(String field, T obj) throws IllegalAccessException {
        return morphium.getConfig().getMapper().getField(type, field).get(obj);
    }

    public void setValue(Enum field, Object value, T obj) throws IllegalAccessException {
        setValue(field.name(), value, obj);
    }

    public void setValue(String field, Object value, T obj) throws IllegalAccessException {
        morphium.getConfig().getMapper().getField(type, field).set(obj, value);
    }

    public boolean existsField(String field) {
        return morphium.getConfig().getMapper().getField(type, field) != null;
    }

}
