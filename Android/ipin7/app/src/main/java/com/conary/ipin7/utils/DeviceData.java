package com.conary.ipin7.utils;

public interface DeviceData
{
    int DEVICE_CONNECTEING = 0;
    int DEVICE_CONNECTED = 1;
    int DEVICE_DISCONNECTED = 2;
    int DEVICE_UPDATE_DATA  = 3;

    int RX_MAX_BYTE_LENGTH = 12;
    int LaserQuality = 500;//257; // 257 means 0x0101

    String DECODE_ERROR_DATA = "FFF4";
    String DECODE_LASER_ON_DATA = "AA0001BE00010001C1";
    String DECODE_LASER_OFF_DATA = "AA0001BE00010000C0";
    String DECODE_ONE_SHOT_PACKAGE_DATA = "AA0000220003";
    String DECODE_LASER_ON_OFF_PACKAGE_DATA = "AA0001BE000100";

    byte[] STOP_CONTINUOUS_MEASURE = {0x58};
    byte[] FAST_CONTINUOUS_MEASURE = {(byte) 0xAA,0x00,0x00,0x20,0x00,0x01,0x00,0x06,0x27};
    byte[] ONE_SHOT_MEASURE = {(byte) 0xAA,0x00,0x00,0x20,0x00,0x01,0x00,0x00,0x21};
    byte[] READ_SW_VERSION = {(byte) 0xAA,(byte)0x80,0x00,0x0C,(byte)0x8C};
    byte[] OPEN_LASER = {(byte) 0xAA,(byte)0x00,0x01,(byte)0xBE,(byte)0x00,0x01,0x00,0x01,(byte)0xc1};
    byte[] CLOSE_LASER = {(byte) 0xAA,(byte)0x00,0x01,(byte)0xBE,(byte)0x00,0x01,0x00,0x00,(byte)0xc0};

}
