package kz.kis.kisauth.controller;

import kz.kis.kisauth.service.GrpService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grp")
@AllArgsConstructor
public class GrpController {

    private final GrpService grpService;

    @GetMapping("/create")
    public ResponseEntity<Void> createGrp(
            @RequestParam(name = "name") String name
    ) {
        return ResponseEntity.ok().build();
    }
}