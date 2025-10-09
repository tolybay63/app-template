package kz.app.appnsi.controller;

import kz.app.appcore.model.DbRec;
import kz.app.appnsi.service.NsiDao;
import kz.app.appnsi.service.SourceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private NsiDao nsiDao;

    @GetMapping(value = "/loadSourceCollections")
    public List<DbRec> loadSourceCollections(@RequestParam long obj) throws Exception {
        return nsiDao.loadSourceCollections(obj);
    }

    @GetMapping(value = "/loadDepartments")
    public List<DbRec> loadDepartments(@RequestParam String codTyp, String codProp) throws Exception {
        return nsiDao.loadDepartments(codTyp, codProp);
    }

    @GetMapping(value = "/loadDepartmentsWithFile")
    public DbRec loadDepartmentsWithFile(@RequestParam long obj) throws Exception {
        return nsiDao.loadDepartmentsWithFile(obj);
    }

    @GetMapping(value = "/loadAttachedFiles")
    public List<DbRec> loadAttachedFiles(@RequestParam long obj, @RequestParam String codProp) throws Exception {
        return nsiDao.loadAttachedFiles(obj, codProp);
    }

    @PostMapping(value = "/saveDepartment")
    public void saveDepartment(@RequestBody DbRec rec) throws Exception {
        nsiDao.saveDepartment(rec);
    }

    @PostMapping(value = "/saveSourceCollections")
    public List<DbRec> saveSourceCollections(@RequestBody DbRec rec) throws Exception {
        String mode = rec.getString("mode");
        rec.remove("mode");
        return nsiDao.saveSourceCollections(mode, rec);
    }

    @GetMapping(value = "/deleteOwnerWithProperties")
    public void deleteOwnerWithProperties(@RequestParam long obj) throws Exception {
        nsiDao.deleteOwnerWithProperties(obj);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam MultipartFile file,
            @RequestParam long obj, @RequestParam String cod) throws Exception {
        // Обработка файла

        String dir = "C:" + File.separator + "minio_storage" + File.separator + "dtj" + File.separator;
        if (!(new File(dir).exists()))
            new File(dir).mkdir();
        String fileName = file.getOriginalFilename();
        long idFileVal = nsiDao.toDbFileStorage(dir, fileName);
        DbRec rec = new DbRec();
        rec.put("own", obj);
        rec.put("codProp", cod);
        rec.put("fileVal", idFileVal);
        nsiDao.attachFile(rec);
        file.transferTo(new File(dir + idFileVal + "_" + fileName));
        return ResponseEntity.ok("Файл успешно загружен!");
    }

    @GetMapping(value = "/deleteFileValue")
    public void deleteFileValue(@RequestParam long idDPV, long fileVal) throws Exception {
        nsiDao.deleteFileValue(idDPV, fileVal);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String filename) throws IOException {
        String dir = "C:" + File.separator + "minio_storage" + File.separator + "dtj" + File.separator;
        Path filePath = Paths.get(dir + filename);
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }


}
