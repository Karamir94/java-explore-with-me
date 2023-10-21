package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {

    private Long id;

    @NotBlank
    private String ip;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotNull
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

