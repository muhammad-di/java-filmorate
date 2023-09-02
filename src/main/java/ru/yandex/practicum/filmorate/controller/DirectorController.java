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
    public Collection<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Long id)
            throws DirectorDoesNotExistException, IncorrectParameterException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return service.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) throws DirectorAlreadyExistException {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) throws DirectorDoesNotExistException {
        return service.updateDirector(director);
    }
}
