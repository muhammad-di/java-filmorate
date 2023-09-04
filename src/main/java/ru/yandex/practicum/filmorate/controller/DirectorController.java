package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DirectorDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.Constants.MIN_ID;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    @Autowired
    private final DirectorService service;

    @GetMapping
    public Collection<Director> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable Long id)
            throws DirectorDoesNotExistException, IncorrectParameterException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return service.findById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) throws DirectorAlreadyExistException {
        return service.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) throws DirectorDoesNotExistException {
        return service.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id)
            throws IncorrectParameterException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        service.deleteById(id);
    }
}
