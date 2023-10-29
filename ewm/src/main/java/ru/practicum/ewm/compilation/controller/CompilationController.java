package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "10", required = false) Integer size,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(required = false) Boolean pinned) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }
}
