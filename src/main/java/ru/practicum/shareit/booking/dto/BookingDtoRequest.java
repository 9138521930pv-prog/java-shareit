package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDtoRequest {
    private Integer id;
    @FutureOrPresent
    @NotNull
    @JsonFormat
    private LocalDateTime start;
    @Future
    @NotNull
    @JsonFormat
    private LocalDateTime end;
    @NotNull
    private Integer itemId;
    private Integer bookerId;
}
