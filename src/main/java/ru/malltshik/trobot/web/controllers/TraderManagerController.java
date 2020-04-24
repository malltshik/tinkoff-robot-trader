package ru.malltshik.trobot.web.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.repositories.TraderConfigRepository;
import ru.malltshik.trobot.trading.Trader;
import ru.malltshik.trobot.trading.TraderManager;
import ru.malltshik.trobot.web.transport.TraderConfigCreateRequest;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/traders")
@RequiredArgsConstructor
public class TraderManagerController {

    private final ModelMapper mapper;
    private final TraderManager traderManager;
    private final TraderConfigRepository traderConfigRepository;

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody TraderConfigCreateRequest request) {
        TraderConfig config = mapper.map(request, TraderConfig.class);
        config = traderConfigRepository.save(config);
        return ResponseEntity.ok(traderManager.register(config).getState());
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<?> state(@PathVariable Long id) {
        Optional<Trader> optional = traderManager.findOne(id);
        return optional.map(t -> ResponseEntity.ok(t.getState())).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> start(@PathVariable Long id) {
        Optional<Trader> optional = traderManager.findOne(id);
        optional.ifPresent(Trader::start);
        return optional.map(t -> ResponseEntity.ok(t.getState())).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stop(@PathVariable Long id) {
        Optional<Trader> optional = traderManager.findOne(id);
        optional.ifPresent(Trader::stop);
        return optional.map(t -> ResponseEntity.ok(t.getState())).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        traderManager.unregister(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
