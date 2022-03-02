/*
 * MIT License
 *
 * Copyright (c) 2022 Husby Labs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 *  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 */

package com.husbylabs.warptables.events.state;

import com.husbylabs.warptables.Status;
import com.husbylabs.warptables.WarpTableInstance;
import com.husbylabs.warptables.events.Event;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author Noah Husby
 */
public class StatusChangedEvent extends Event {
    @Getter
    private final Status status;

    public StatusChangedEvent(@NonNull WarpTableInstance instance, Status status) {
        super(instance);
        this.status = status;
    }
}
