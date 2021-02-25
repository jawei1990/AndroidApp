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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TestScreen extends Fragment implements ServiceConnection, SerialListener
{
    private Context context;
    private int baudRate = 256000;
    private enum Connected { False, Pending, True }

    private final BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;
    private SerialService service;

    private TextView receiveText;
    private TextView tv_status;
    private Button btnOn,btnOff, btnCal,btnCleanDis,btnOutput;
    private EditText ed_sub,ed_device,ed_real;
    private ListView listView;
    private measureAdapter listAdapter;
    private List<ListMeasure> measureList = new ArrayList<>();

    private TestScreen.Connected connected = TestScreen.Connected.False;
    private boolean initialStart = true;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;

    public TestScreen()
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
        if (connected != TestScreen.Connected.False)
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

    private int offset = 0;
    private int onStatus = 0; // on = 0, measure = 1
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_test, container, false);

        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_status = view.findViewById(R.id.tv_dev);
        ed_sub = view.findViewById(R.id.ed_sub);
        ed_device = view.findViewById(R.id.ed_device);
        ed_real = view.findViewById(R.id.ed_real);

        listAdapter = new measureAdapter(context,measureList);
        listView = view.findViewById(R.id.dis_list);
        listView.setAdapter(listAdapter);

        btnOn = view.findViewById(R.id.btnLaserOn);
        btnOff = view.findViewById(R.id.btnLaserOff);
        btnCal = view.findViewById(R.id.btnCali);

        btnCleanDis = view.findViewById(R.id.btnCleanDis);
        btnOutput = view.findViewById(R.id.btnLogOutPut);

        btnOn.setOnClickListener(v ->
        {
            if(connected == Connected.True)
            {
                if(onStatus == 0)
                {
                    btnCal.setEnabled(false);
                    send("E");

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
                    btnCal.setEnabled(false);
                    onStatus = 0;
                }
            }
        }
        );

        btnOff.setOnClickListener(v ->{
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
            btnCal.setEnabled(false);
        });

        offset = mUserPreferences.getPrefOffset();
        btnOff.setOnLongClickListener(v->{
            LinearLayout dialogDev = new LinearLayout(context);
            dialogDev.setOrientation(LinearLayout.VERTICAL);
            final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(450,200);
            LinearLayout L1 = new LinearLayout(context);
            L1.setOrientation(LinearLayout.HORIZONTAL);
            TextView t1 = new TextView(context);
            t1.setWidth(200);
            t1.setHeight(100);
            t1.setText("Offset(mm):");
            L1.addView(t1);
            final EditText ed_min_dis = new EditText(context);
            ed_min_dis.setLayoutParams(lparams);
            ed_min_dis.setHint(String.valueOf(offset));
            ed_min_dis.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            L1.addView(ed_min_dis);
            dialogDev.addView(L1);

            AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(context);
            alert.setTitle("Set Offset:");

            alert.setView(dialogDev);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!ed_min_dis.getText().toString().equals(""))
                    {
                        offset = Integer.valueOf(ed_min_dis.getText().toString());
                        mUserPreferences.setPrefOffset(offset);
                    }
                }
            });

            return false;
        });

        btnCleanDis.setOnClickListener(v -> {
            measureList.removeAll(measureList);
            listAdapter.notifyDataSetChanged();
        });

        btnOutput.setOnClickListener(v -> {
            DataLog.debug("Date/Time;" + getTime() + "\n");
            DataLog.debug("Subject;" + ed_sub.getText().toString() +"\n");
            DataLog.debug("Module No;" + ed_device.getText().toString()+"\n");
            DataLog.debug("Real D;" + ed_real.getText().toString() + "\n");
            DataLog.debug("Offset;" + String.valueOf(offset) + "\n");

            String dis = "Measure D;";
            for(int i = 0; i < measureList.size(); i++)
                dis+= measureList.get(i).getDis().toString() + ";";
            DataLog.debug(dis + "\n");
            DataLog.debug("---------------------------------"+ "\n");

            AlertDialog.Builder alert;
            alert = new AlertDialog.Builder(context);
            alert.setTitle("Log Out Put Done !!!");
            alert.setPositiveButton("OK", null);
            alert.show();
        });
        return view;
    }

    private String getTime()
    {
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar calDate = Calendar.getInstance();
        String time = date.format(calDate.getTime());
        return time;
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
                    if((device.getVendorId() == 4292) && (device.getProductId() == 60000))
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
            if((v.getVendorId() == 4292) && (v.getProductId() == 60000))
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

        connected = TestScreen.Connected.Pending;
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
        connected = TestScreen.Connected.False;
        service.disconnect();
        usbSerialPort = null;
        initialStart = true;
        Log.e("Awei","----- service.disconnect -----");
    }

    private void send(String str) {
        if(connected != TestScreen.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            tv_status.setText("Disconnected");
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

    private void receive(byte[] data) {
        String msg = new String(data);
        Log.e("Awei","receiveText:" + msg );

        if(msg.contains("Calib Done"))
        {
            btnOn.setEnabled(true);
            btnOff.setEnabled(true);
            btnCal.setEnabled(true);
        }

     //   receiveText.setText(msg);
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

            if(msg.contains("DIST:"))
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
                    String str_dis = String.valueOf(dis);

                    ListMeasure list  = new ListMeasure(String.valueOf(measureList.size()),str_dis);
                    measureList.add(list);
                    listAdapter.notifyDataSetChanged();

                }
                catch (Exception e)
                {
                    String str_dis = elements_line[0];
                }
                Log.e("Awei","receiveText:" +elements_line[0] );

                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
                btnCal.setEnabled(true);
                btnOn.setText("Laser On");
            }

        }
    }

    void detected (String str)
    {
        tv_status.setText(str);
        tv_status.setBackgroundColor(getResources().getColor(R.color.colorRecieveText));
    }

    void lost (String str)
    {
        tv_status.setText(str);
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
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
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        tv_status.setText("Disconnected");
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
    }

    @Override
    public void onSerialConnect() {
        status("Connected");
        connected = TestScreen.Connected.True;
        tv_status.setBackgroundColor(getResources().getColor(R.color.colorRecieveText));
    }

    @Override
    public void onSerialConnectError(Exception e) {
        //status("connection failed: " + e.getMessage());
        status("Connection failed");
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
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
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
        disconnect();
    }
}