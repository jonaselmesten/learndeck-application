package connection;

import java.util.List;
import java.util.Optional;

interface Dao<T> {

    Optional<T> get(long id);

    List<T> getAll();

    List<T> getAllFromUser(long userId) throws ConnectionException;

    void update(T t, String[] params);

    void delete(T t);

}
