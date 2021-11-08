package com.example.laserusbdemo;

public interface Utils {
    // 0: init, 1: laser on, 2: measure, 3: shots, 4: calibration, 5: enter debug mode, 6: get phase data, 7: get temp, 8:get version
    int STATUS_INIT = 0;
    int STATUS_LASER_ON = 1;
    int STATUS_MEASURE = 2;
    int STATUS_SHOTS = 3;
    int STATUS_CALIB = 4;
    int STATUS_DEBUG_MODE = 5;
    int STATUS_PHASE_DATA = 6;
    int STATUS_TEMP_DATA = 7;
    int STATUS_VERSION_DATA = 8;
}
