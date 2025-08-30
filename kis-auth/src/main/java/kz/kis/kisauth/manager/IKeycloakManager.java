package kz.kis.kisauth.manager;

import jakarta.servlet.http.*;
import kz.kis.kiscore.model.*;

import java.util.*;


public interface IKeycloakManager {

    void createUser(Long usrId, String username, String password, String email, String firstName, String lastName);

    void setUserID(String keycloakUserId, Long userId);

    void logout();

    void resetPassword(String password, String id);

    void deleteUser(String id);

    void addIgnoreLDAP(String userName);

    void deleteFromIgnoreLDAP(DbRec userData);

    Boolean isUserAD(DbRec userData);

    DbRec getUserByName(String username, Long id);

    DbRec authenticate(String username, String password);

    DbRec refreshToken(String refreshToken);

    DbRec getUserData(String keycloakUserId);

    List<DbRec> findAllUsers(int offset, int size);

    List<DbRec> findUserGroups(String userId);
}