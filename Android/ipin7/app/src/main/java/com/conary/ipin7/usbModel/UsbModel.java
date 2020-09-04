package com.conary.ipin7.usbModel;

public interface UsbModel
{
    void StartUsbConnection();
    void StopUsbConnection();
    void USB_ViewInit(UsbModelImpl.UsbView view);
    void SerialDataWrite(byte[] data);
    void USB_ContinuousMeasure();
    void USB_OneShotMeasure();
    void USB_StopMeasure();
    boolean GetUsbConnection();
}
