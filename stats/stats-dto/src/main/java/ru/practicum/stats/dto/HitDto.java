package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {

    Long id;

    @NotBlank
    String ip;

    @NotBlank
    String app;

    @NotBlank
    String uri;

    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}

