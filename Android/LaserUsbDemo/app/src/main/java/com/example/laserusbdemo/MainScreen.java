package com.example.laserusbdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainScreen extends Fragment implements ServiceConnection, SerialListener
{
    private Context context;
    private int baudRate = 256000;
    private enum Connected { False, Pending, True }

    private final BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;
    private SerialService service;

    private RelativeLayout layout,offsetLayout,debugLayout;
    private LinearLayout calLayout;
    private TextView receiveText,tv_hint;
    private Button btnOn, btnOff, btnCal,btnOK,btnShots,btnLog,btn_back;
    private ImageView img_device,img_rotation;
    private TextView tv_status,tv_ver,tv_log;
    private EditText ed_offset;

    private MainScreen.Connected connected = MainScreen.Connected.False;
    private boolean initialStart = true;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;

    public MainScreen()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.INTENT_ACTION_GRANT_USB)) {
                    Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    connect(granted);
                }
                else if(intent.getAction().equals(Constants.INTENT_ACTION_CONNECT))
                {
                    detected("Connected");
                }
                else if(intent.getAction().equals(Constants.INTENT_ACTION_DISCONNECT))
                {
                    lost("Device Lost");
                }
            }
        };
    }

    private UserPreferences mUserPreferences;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
        this.context = MainActivity.context;
        mUserPreferences = new UserPreferences(context);
    }

    @Override
    public void onDestroy() {
        if (connected != MainScreen.Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("Awei","--- onResume----");
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_DISCONNECT));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_CONNECT));
        refresh();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    private int MAX_CNT = 500;
    private int cnt = 0;
    private float rotation = 180.0f;
    private boolean isRotation = false;
    private boolean isShots = false;
    private boolean isDevMode = false;
    private int onStatus = 0; // on = 0, measure = 1
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_main, container, false);

        offsetLayout = view.findViewById(R.id.offsetLayout);
        layout = view.findViewById(R.id.screen_main);
        debugLayout = view.findViewById(R.id.debugLayout);

        boolean is360Rotation = mUserPreferences.getPrefRotationData();
        if(is360Rotation)
        {
            isRotation = true;
            rotation = 0.0f;
        }
        else
        {
            rotation = 180.f;
            isRotation = false;
        }

        debugLayout.setRotation(rotation);
        layout.setRotation(rotation);
        offsetLayout.setRotation(rotation);
        receiveText = view.findViewById(R.id.tv_title);
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_status = view.findViewById(R.id.tv_device_status);
        img_device = view.findViewById(R.id.img_device);
        img_rotation = view.findViewById(R.id.img_rotation);

        tv_ver = view.findViewById(R.id.tv_ver);

        calLayout =  view.findViewById(R.id.calLayout);
        btnShots =  view.findViewById(R.id.btnShots);

        offset = mUserPreferences.getPrefOffsetData();
        Log.e("Awei","Offset:" + offset);
        ed_offset = view.findViewById(R.id.ed_offset);
        ed_offset.setHint(String.valueOf(offset));
        tv_hint = view.findViewById(R.id.tv_hint);
        btnOK = view.findViewById(R.id.btnOK);
        btnOn = view.findViewById(R.id.btnOn);
        btnOff = view.findViewById(R.id.btnOff);
        btnCal = view.findViewById(R.id.btnCal);
        btnLog = view.findViewById(R.id.btnLog);
        btn_back = view.findViewById(R.id.btn_back);
        tv_log = view.findViewById(R.id.tv_log);

        boolean isVisible = mUserPreferences.getPrefVisibleData();
        if(isVisible)
        {
            calLayout.setVisibility(View.VISIBLE);
            isDevMode = true;
        }
        else
        {
            calLayout.setVisibility(View.INVISIBLE);
            isDevMode = false;
        }

        img_rotation.setOnClickListener(v ->
        {
            if(isRotation)
            {
                rotation = 180.f;
                isRotation = false;
                mUserPreferences.setPrefRotationData(isRotation);
                layout.setRotation(rotation);
                offsetLayout.setRotation(rotation);
                debugLayout.setRotation(rotation);
            }
            else
            {
                isRotation = true;
                rotation = 0.0f;
                mUserPreferences.setPrefRotationData(isRotation);
                layout.setRotation(rotation);
                offsetLayout.setRotation(rotation);
                debugLayout.setRotation(rotation);
            }
        });

        btnShots.setOnClickListener(v ->
        {
            isShots = true;
            DataLog.e("===== btnShots ====" + getTime());
            do_test();
        });

        btnOK.setOnClickListener(v->{
            if(!ed_offset.getText().toString().equals(""))
            {
                try
                {
                    offset = Integer.valueOf(ed_offset.getText().toString());
                    mUserPreferences.setPrefOffsetData(offset);
                    offsetLayout.setVisibility(View.INVISIBLE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    tv_hint.setText("Wrong value...");
                }
            }
        });

        btnOn.setOnClickListener(v ->
                {
                    if(onStatus == 0)
                    {
                        btnCal.setEnabled(false);
                        send("E");
                        receiveText.setText("");

                        try
                        {
                            Thread.sleep(500);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                        btnOn.setText("Measure");
                        onStatus = 1;
                    }
                    else if (onStatus == 1)
                    {
                        send("S");
                        btnOn.setEnabled(false);
                        btnOff.setEnabled(false);
                        btnCal.setEnabled(false);
                        onStatus = 0;
                    }
                }
        );

        btnOff.setOnClickListener(v ->
        {
            isShots = false;
            cnt = 0;
            onStatus = 0;
            send("D");
            btnOn.setText("Laser On");
            btnOn.setEnabled(true);
            btnOff.setEnabled(true);
            btnCal.setEnabled(true);
        });

        btnCal.setOnClickListener(v ->
        {
            send("C");
            btnOn.setEnabled(false);
            btnOff.setEnabled(false);
            btnCal.setEnabled(false);
        });

        btnOff.setOnLongClickListener(v->{
            if(isDevMode)
                offsetLayout.setVisibility(View.VISIBLE);
            return false;
        });

        tv_status.setOnLongClickListener(v ->
        {
            if(isDevMode)
            {
                calLayout.setVisibility(View.INVISIBLE);
                isDevMode = false;
                mUserPreferences.setPrefVisibleData(isDevMode);
            }
            else
            {
                calLayout.setVisibility(View.VISIBLE);
                isDevMode = true;
                mUserPreferences.setPrefVisibleData(true);
            }

            return false;
        });

        btnLog.setOnClickListener(v -> debugLayout.setVisibility(View.VISIBLE));
        btn_back.setOnClickListener(v -> debugLayout.setVisibility(View.INVISIBLE));

        return view;
    }

    private String getTime()
    {
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
        Calendar calDate = Calendar.getInstance();
        String time = date.format(calDate.getTime());
        return time;
    }

    private void do_test()
    {
        try
        {
            send("E");
            Thread.sleep(1000);
            send("S");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void refresh()
    {
        if(connected == Connected.True)
            return;

        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        for(UsbDevice device : usbManager.getDeviceList().values())
        {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if(driver == null)
            {
                driver = usbCustomProber.probeDevice(device);
            }
            if(driver != null)
            {
                for(int port = 0; port < driver.getPorts().size(); port++)
                {
                    if((device.getVendorId() == 6790) && (device.getProductId() == 29987))
                    {
                        deviceId = device.getDeviceId();
                        portNum = port;

                        if(initialStart && service != null)
                        {
                            getActivity().runOnUiThread(this::connect);
                        }
                    }
                }
            }
        }
    }

    private void connect() {
        initialStart = false;
        connect(null);
    }

    private void connect(Boolean permissionGranted) {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        for(UsbDevice v : usbManager.getDeviceList().values())
        {
            if((v.getVendorId() == 6790) && (v.getProductId() == 29987))
                device = v;
        }

        if(device == null) {
            //status("connection failed: device not found");
            status("Device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
           // status("connection failed: no driver for device");
            status(" No driver for device");
            return;
        }
        if(driver.getPorts().size() < portNum) {
            //status("connection failed: not enough ports at device");
            status(" Not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null && permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
            {
                //status("connection failed: permission denied");
                status("Permission denied");
            }
            else
            {
                //status("connection failed: open failed");
                status("Open failed");
            }

            return;
        }

        connected = MainScreen.Connected.Pending;
        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = MainScreen.Connected.False;
        service.disconnect();
        usbSerialPort = null;
        initialStart = true;
        Log.e("Awei","----- service.disconnect -----");
    }

    private void send(String str) {
        if(connected != MainScreen.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            tv_status.setText("Disconnected");
            img_device.setBackgroundResource(R.drawable.disconnect);
            return;
        }
        try {
            byte[] data;
            data = (str + newline).getBytes();
            Log.e("Awei","send:" + str);
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    int offset;
    private void receive(byte[] data) {
        String msg = new String(data);
        Log.e("Awei","receiveText:" + msg );

        if(msg.contains("Calib Done"))
        {
            receiveText.setText("Calib Done");
            btnOn.setEnabled(true);
            btnOff.setEnabled(true);
            btnCal.setEnabled(true);
        }


        if(msg.contains("DB:"))
        {
            String str = msg + "\n" +tv_log.getText().toString();
            tv_log.setText(str);
        }

        if(newline.equals(TextUtil.newline_crlf) && msg.length() > 0)
        {
            // don't show CR as ^M if directly before LF
            msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
            // special handling if CR and LF come in separate fragments
            if (pendingNewline && msg.charAt(0) == '\n') {
                Editable edt = receiveText.getEditableText();
                if (edt != null && edt.length() > 1)
                    edt.replace(edt.length() - 2, edt.length(), "");
            }
            pendingNewline = msg.charAt(msg.length() - 1) == '\r';

            if(msg.contains("DIST"))
            {
                String[] elements = msg.split(":");
                String str_data = elements[1];

                String pattern_line = "mm";
                String[] elements_line = str_data.split(pattern_line);

                try
                {
                    double d = Double.valueOf(elements_line[0]);
                    d += offset;

                    double dis = (double) d / 10;
                    String str_dis = String.valueOf(dis) + " cm";
                    receiveText.setText(str_dis);
                    btnOn.setEnabled(true);
                    btnOff.setEnabled(true);
                    btnCal.setEnabled(true);
                    btnOn.setText("Laser On");

                    if(isShots)
                    {
                        DataLog.e("[" + cnt+ "]:" + str_dis);
                        cnt++;
                        String str = cnt + "- Shots";
                        btnShots.setText(str);

                        if(cnt <= MAX_CNT )
                        {
                            do_test();
                        }
                        else
                        {
                            DataLog.e(" ===== Finish ======" + getTime());
                            cnt = 0;
                            btnShots.setText("shots");
                        }
                    }
                    else
                    {
                        DataLog.e("Single Shot:" + str_dis);
                    }
                }
                catch (Exception e)
                {
                    String str_dis = elements_line[0];
                    receiveText.setText(str_dis);
                    btnOn.setEnabled(true);
                    btnOff.setEnabled(true);
                    btnCal.setEnabled(true);
                }
                Log.e("Awei","receiveText:" +elements_line[0] );


            }
            else if(msg.contains("V"))
            {
                tv_ver.setText(msg);
            }

        }
    }

    void detected (String str)
    {
        tv_status.setText(str);
        img_device.setBackgroundResource(R.drawable.connected);
    }

    void lost (String str)
    {
        tv_status.setText(str);
        img_device.setBackgroundResource(R.drawable.disconnect);
    }

    void status(String str) {
        tv_status.setText(str);
        Log.e("Awei","" + str);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);

        if(initialStart && isResumed()) {
            getActivity().runOnUiThread(this::connect);
            send("V");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        tv_status.setText("Disconnected");
        img_device.setBackgroundResource(R.drawable.disconnect);
    }

    @Override
    public void onSerialConnect() {
        status("Connected");
        img_device.setBackgroundResource(R.drawable.connected);
        connected = MainScreen.Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        //status("connection failed: " + e.getMessage());
        status("Connection failed");
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        //status("connection lost: " + e.getMessage());
        status("Connection Lost");
        img_device.setBackgroundResource(R.drawable.disconnect);
        disconnect();
    }
}