package kz.kis.kisauth.connection;

import kz.kis.kisauth.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.*;
import org.springframework.jdbc.datasource.*;

import javax.sql.*;
import java.sql.*;

public class UserInfoDataSource extends DelegatingDataSource {

    private static final Logger log = LoggerFactory.getLogger(UserInfoDataSource.class);

    private final ObjectProvider<UserService> userServiceProvider;

    public UserInfoDataSource(DataSource targetDataSource, ObjectProvider<UserService> userServiceProvider) {
        super(targetDataSource);
        this.userServiceProvider = userServiceProvider;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        initConnectionWithUserId(connection);
        return connection;
    }

    private void initConnectionWithUserId(Connection connection) {
        long userId = getCurrentUser();
        if (userId != -1) {
            log.info("Set current user to connection, userId: {}", userId);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS app_user");
                stmt.execute("CREATE TEMP TABLE app_user(id bigint)");
                stmt.execute("INSERT INTO app_user (id) VALUES (" + userId + ")");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private long getCurrentUser() {
        UserService userService = userServiceProvider.getIfAvailable();
        if (userService != null) {
            return userService.getCurrentUsrIdFromAuth();
        } else {
            return -1;
        }
    }

}