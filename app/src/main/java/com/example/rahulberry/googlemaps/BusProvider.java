package com.example.rahulberry.googlemaps;

import com.squareup.otto.Bus;

/**
 * Created by rahulberry on 28/02/2018.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}