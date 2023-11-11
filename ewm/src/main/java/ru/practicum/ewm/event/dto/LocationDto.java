package ru.practicum.ewm.event.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull
    @Min(-180)
    @Max(180)
    private float lon;

    @NotNull
    @Min(-90)
    @Max(90)
    private float lat;
}
