package ru.malltshik.trobot.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final BuildProperties build;

    @GetMapping("/build")
    public ResponseEntity<?> build() {
        return ResponseEntity.ok(build);
    }

}
