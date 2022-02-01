package com.husbylabs.warptables;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum State {
    HANDSHAKE(0), STATUS(1), READY(2);

    private final int id;
}
