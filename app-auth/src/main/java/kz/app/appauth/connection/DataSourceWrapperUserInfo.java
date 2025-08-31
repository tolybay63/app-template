package kz.app.appauth.connection;

import kz.app.appauth.service.*;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;

import javax.sql.*;

@Component
public class DataSourceWrapperUserInfo implements DataSourceWrapper {

    private final ObjectProvider<UserService> userServiceProvider;

    public DataSourceWrapperUserInfo(ObjectProvider<UserService> userServiceProvider) {
        this.userServiceProvider = userServiceProvider;
    }

    @Override
    public DataSource getDataSource(DataSource targetDataSource) {
        return new UserInfoDataSource(targetDataSource, userServiceProvider);
    }

}
