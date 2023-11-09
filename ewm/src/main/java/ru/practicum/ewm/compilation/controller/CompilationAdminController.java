package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequest;
import ru.practicum.ewm.compilation.dto.SavedCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @ResponseStatus(CREATED)
    @PostMapping("/compilations")
    public CompilationDto saveCompilation(@Valid @RequestBody SavedCompilationDto savedCompilationDto) {
        return compilationService.saveCompilation(savedCompilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody CompilationUpdateRequest compilationUpdateRequest,
                                            @PathVariable Long compId) {
        return compilationService.updateCompilation(compId, compilationUpdateRequest);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
