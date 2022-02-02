package com.husbylabs.warptables.providers;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Noah Husby
 */
@AllArgsConstructor
@Data
public class ProviderMetadata {
    private boolean serverSupported;
    private boolean clientSupported;
    private boolean multiClientsSupported;
}
