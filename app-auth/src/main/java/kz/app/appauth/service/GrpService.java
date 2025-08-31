package kz.app.appauth.service;

import jakarta.annotation.*;
import kz.app.appcore.model.*;
import kz.app.appcore.utils.*;
import kz.app.appdbtools.repository.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@AllArgsConstructor
public class GrpService {

    private final Db db;

    public Long findGrp(String name) throws Exception {
        List<DbRec> result = db.loadList("Grp", UtCnv.toMap("name", name));

        if (result.isEmpty()) {
            return 0L;
        }

        return UtCnv.toLong(result.getFirst().get("id"));
    }

    public Long createGrp(String name) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return db.insertRec("Grp", params);
    }

    public Boolean grpContainsUsr(Long grp, Long usr) throws Exception {
        List<DbRec> result = db.loadList("UsrGrp", UtCnv.toMap("grp", grp, "usr", usr));

        if (result.isEmpty()) {
            return false;
        }
        return true;
    }

    public void deleteUsrFromGrp(Long grp, Long usr) throws Exception {
        db.deleteRec("UsrGrp", UtCnv.toMap("grp", grp, "usr", usr));
    }

    public void addUserInGroup(Long grp, Long usr) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("grp", grp);
        params.put("usr", usr);

        db.insertRec("UsrGrp", params);
    }

    public Map<Long, Boolean> getGrpsByUsr(Long usr) throws Exception {
        List<DbRec> usrGrps = db.loadList("UsrGrp", UtCnv.toMap("usr", usr));

        Map<Long, Boolean> result = new HashMap<>();

        if (usrGrps.isEmpty()) {
            return result;
        }

        for (Map<String, Object> grp : usrGrps) {
            result.put(UtCnv.toLong(grp.get("grp")), true);
        }

        return result;
    }

    public Map<Long, List<Long>> getGrpsByUsr(@Nullable List<Long> usrs) throws Exception {
        Map<Long, List<Long>> usrGrps = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        if (usrs != null && !usrs.isEmpty()) {
            params.put("usr", usrs);
        }
        List<DbRec> list = db.loadList("UsrGrp", params);

        for (Map<String, Object> result : list) {
            Long usr = UtCnv.toLong(result.get("usr"));
            List<Long> grps;
            if (!usrGrps.containsKey(usr)) {
                grps = new ArrayList<>();
                usrGrps.put(usr, grps);
            } else {
                grps = usrGrps.get(usr);
            }

            grps.add(UtCnv.toLong(result.get("grp")));
        }

        return usrGrps;
    }
}