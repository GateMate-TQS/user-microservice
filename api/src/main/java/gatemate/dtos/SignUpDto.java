package gatemate.dtos;

import gatemate.data.UserRole;

import com.fasterxml.jackson.annotation.JsonFormat;

public record SignUpDto(
                String login,
                String password,
                @JsonFormat(shape = JsonFormat.Shape.STRING) UserRole role) {
}