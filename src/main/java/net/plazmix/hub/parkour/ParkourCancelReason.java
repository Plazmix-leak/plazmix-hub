package net.plazmix.hub.parkour;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ParkourCancelReason {

    TOGGLE_FLIGHT("Режим полета на мини-паркуре запрещен"),
    LEAVE_GAME("Нельзя покидать игру во время мини-паркура :("),
    FALLING("Ничего страшного, если упал, попробуешь себя снова немного позже :)"),
    ;

    String reason;
}
