package connection;

import model.User;

import java.util.List;
import java.util.Optional;

public class UserDao implements Dao<User>{

    WebserviceConnection connection = new WebserviceConnection();

    @Override
    public Optional<User> get(long id) {
        return connection.getUser(id);
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public List<User> getAllFromUser(long userId) {
        return null;
    }

    @Override
    public void update(User user, String[] params) {

    }

    @Override
    public void delete(User user) {

    }
}
