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
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private int baudRate = 115200;
    private enum Connected { False, Pending, True }

    private final BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;
    private SerialService service;

    private TextView receiveText;

    private TextView tv_status,tv_ver,tvAppVer;
    private Button btnOn,btnOff, btnCal,btnCleanDis,btnOutput,BtnShots,BtnGradeSet,btnForce;
    private ToggleButton btnDisEn,btnPhase;
    private EditText ed_sub,ed_device,ed_real;
    private ListView listView;
    private Spinner spinner,grade;
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

        DataLog.e("--- onResume----");
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

    void FragmentOnKeyDown(int key_code){
        //do whatever you want here
        if (key_code == KeyEvent.KEYCODE_VOLUME_DOWN || key_code == KeyEvent.KEYCODE_VOLUME_UP){
            btnOn.callOnClick();
        }
    }

    private void measure()
    {
        DataLog.e("measure:" + status_mode);
        if (status_mode == 0)
        {
            receiveText.setText("");
            //BtnShots.setEnabled(false);
            btnCal.setEnabled(false);
            //send("EE");
            sendStrByte(uartCmd.lase_on);
            btnOn.setText("Measure");
            status_mode = 1;
        }
        else if (status_mode == 1)
        {
            receiveText.setText("");

            sendStrByte(uartCmd.get_single_distance);
            //send("S");
            btnOn.setEnabled(false);
            btnCal.setEnabled(false);
            status_mode = 2;
        }
    }

    private AdapterView.OnItemSelectedListener gradeOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            grade_idx = pos;
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };

    private AdapterView.OnItemSelectedListener dacGradeOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            dac_grade_idx = pos;
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };

    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            mUserPreferences.setMode(pos);
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //
        }
    };

    // 0: init, 1: laser on, 2: measure, 3: shots, 4: calibration, 5: get version, 6: get phase data
    private int status_mode;
    private final int CNT_MAX = 5;
    private int cnt;
    private int grade_idx, dac_grade_idx;
    private ArrayAdapter<CharSequence> adapter,grade_adapter;
    private int offset = 0;
    //   private int onStatus = 0; // on = 0, measure = 1
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.screen_test, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_status = view.findViewById(R.id.tv_dev);
        tv_ver = view.findViewById(R.id.tv_ver);
        ed_sub = view.findViewById(R.id.ed_sub);
        ed_device = view.findViewById(R.id.ed_device);
        ed_real = view.findViewById(R.id.ed_real);

        tvAppVer = view.findViewById(R.id.app_ver);
        String AppVer = "App Ver:" + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
        tvAppVer.setText(AppVer);

        btnForce = view.findViewById(R.id.btnForce);
        btnForce.setOnClickListener(v -> {
            forceReconnected();
        });

        spinner = view.findViewById(R.id.spinner);
        adapter =  ArrayAdapter.createFromResource(context,
                R.array.mode,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int mode = mUserPreferences.getMode();
        spinner.setSelection(mode, false);
        spinner.setOnItemSelectedListener(spnOnItemSelected);

        grade = view.findViewById(R.id.sp_grade);
        grade_adapter =  ArrayAdapter.createFromResource(context,
                R.array.grade,
                android.R.layout.simple_spinner_item);

        grade_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(grade_adapter);
        grade_idx = 0;
        grade.setSelection(grade_idx, false);
        grade.setOnItemSelectedListener(gradeOnItemSelected);

        dac_grade_idx = 0;

        listAdapter = new measureAdapter(context,measureList);
        listView = view.findViewById(R.id.dis_list);
        listView.setAdapter(listAdapter);


        btnOn = view.findViewById(R.id.btnLaserOn);
        btnOff = view.findViewById(R.id.btnLaserOff);
        btnCal = view.findViewById(R.id.btnCali);
        BtnShots = view.findViewById(R.id.btnShots);
        BtnGradeSet = view.findViewById(R.id.btnSetGrade);
        btnDisEn = view.findViewById(R.id.btnDisEn);
        btnPhase = view.findViewById(R.id.btnPhase);

        btnCleanDis = view.findViewById(R.id.btnCleanDis);
        btnOutput = view.findViewById(R.id.btnLogOutPut);
        btnOn.setOnClickListener(v ->
        {
            measure();
        });

        BtnShots.setOnClickListener(v -> {
            status_mode = 3;
            BtnShots.setEnabled(false);
            btnOn.setEnabled(false);
            btnCal.setEnabled(false);
            //send("O");
            sendStrByte(uartCmd.get_multi_distance);
        });

        BtnGradeSet.setOnClickListener(v->
        {
            String str_tx = "";//"-setenv:";
            if(grade_idx == 0)
            {
                //  str_tx+="laser_grade,2";
                str_tx = uartCmd.laser_grade_2;
            }
            else if(grade_idx == 1)
            {
                //str_tx+="laser_grade,3";
                str_tx = uartCmd.laser_grade_3;
            }

            DataLog.e("Set:" + str_tx);
            //send(str_tx);
            sendStrByte(str_tx);
        });



        btnOff.setOnClickListener(v ->{
            status_mode = 0;
            cnt = 0;
            clean_list();
            //send("D");
            sendStrByte(uartCmd.lase_off);
            btnOn.setText("Laser On");
            BtnShots.setEnabled(true);
            btnOn.setEnabled(true);
            btnOff.setEnabled(true);
            btnCal.setEnabled(true);
        });

        btnDisEn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DataLog.e("Distance Cal Status:" + isChecked);
                if (isChecked) {
                    TestScreen.this.sendStrByte(uartCmd.enable_distance_function);
                } else {
                    TestScreen.this.sendStrByte(uartCmd.disable_distance_function);
                }
            }
        });

        btnPhase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("Awei","Phase:" + isChecked);
                if (isChecked) {
                    TestScreen.this.sendStrByte(uartCmd.enable_phase_correction);
                } else {
                    TestScreen.this.sendStrByte(uartCmd.disable_phase_correction);
                }
            }
        });

        btnCal.setOnClickListener(v ->
        {
            // send("C");
            sendStrByte(uartCmd.zero_cal);
            status_mode = 4;
            btnOn.setEnabled(false);
            btnCal.setEnabled(false);
            BtnShots.setEnabled(false);
        });

        offset = mUserPreferences.getPrefOffset();
        btnCal.setOnLongClickListener(v->{
            LinearLayout dialogDev = new LinearLayout(context);
            dialogDev.setOrientation(LinearLayout.VERTICAL);
            final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(450,200);
            LinearLayout L1 = new LinearLayout(context);
            L1.setOrientation(LinearLayout.HORIZONTAL);
            TextView t1 = new TextView(context);
            t1.setWidth(300);
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

            alert.show();

            return false;
        });

        btnCleanDis.setOnClickListener(v -> {
            receiveText.setText("");
            measureList.removeAll(measureList);
            listAdapter.notifyDataSetChanged();
        });

        btnOutput.setOnClickListener(v -> {
            String[] level_str = getResources().getStringArray(R.array.grade);

            DataLog.debug("Date/Time;" + getTime() + "\n");
            DataLog.debug("Subject;" + ed_sub.getText().toString() +"\n");
            DataLog.debug("Module No;" + ed_device.getText().toString()+"\n");
            DataLog.debug("Grade;" + level_str[grade_idx]+"\n");
            DataLog.debug("Real D;" + ed_real.getText().toString() + "\n");
            DataLog.debug("Offset;" + String.valueOf(offset) + "\n");

            String dis = "Measure D;";
            for(int i = 0; i < measureList.size(); i++)
                dis+= measureList.get(i).getDis().toString() + ";";
            DataLog.debug(dis + "\n");
            DataLog.debug("---------------------------------"+ "\n");

            String time = "Measure temp;";
            for(int i = 0; i < measureList.size(); i++)
                time+= measureList.get(i).getTimes().toString() + ";";
            DataLog.debug(time + "\n");
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

    void forceReconnected()
    {
        disconnect();
        tv_status.setText("Disconnected");
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
        tv_ver.setText("");
        refresh();
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
                    else if((device.getVendorId() == 6790) && (device.getProductId() == 29987))
                    {
                        deviceId = device.getDeviceId();
                        portNum = port;

                        if(initialStart && service != null)
                        {
                            getActivity().runOnUiThread(this::connect);
                        }
                    }
                    else if((device.getVendorId() == 1027) && (device.getProductId() == 24597))
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
            else if((v.getVendorId() == 6790) && (v.getProductId() == 29987))
                device = v;
            else if((v.getVendorId() == 1027) && (v.getProductId() == 24597))
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
            DataLog.e("go to onSerialConnect");
        } catch (Exception e) {
            onSerialConnectError(e);
            DataLog.e("onSerialConnect Err");
        }
    }

    private void disconnect() {
        connected = TestScreen.Connected.False;
        service.disconnect();
        usbSerialPort = null;
        initialStart = true;
        DataLog.e("----- service.disconnect -----");
    }

    private void sendStrByte(String str)
    {
        DataLog.e("Send:" + str);
        if(connected != TestScreen.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            tv_status.setText("Disconnected");
            tv_ver.setText("");
            tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
            return;
        }

        try {

            byte[] data = ConversionUtils.hexToByte(str);
         /*   byte[] n_byte= new byte[data.length + 2];
            System.arraycopy(data, 0, n_byte, 0, data.length);

            n_byte[n_byte.length-2] = 0x0d;
            n_byte[n_byte.length-1] = 0x0a;
            DataLog.e("Send:");
            for(int i = 0; i < n_byte.length; i++)
            {
                DataLog.e("" +  String.format("%X", n_byte[i]));

            }

            DataLog.e("\n");*/
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
            DataLog.e("onSerialIoError");
        }
    }

    private void sendByte(byte[] data) {
        if(connected != TestScreen.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            tv_status.setText("Disconnected");
            tv_ver.setText("");
            tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
            return;
        }

        try {
            service.write(data);
            DataLog.e("Send byte---->");
        } catch (Exception e) {
            onSerialIoError(e);
            DataLog.e("onSerialIoError");
        }
    }

    private void send(String str) {
        if(connected != TestScreen.Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            tv_status.setText("Disconnected");
            tv_ver.setText("");
            tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
            return;
        }
        try {
            byte[] data;
            data = (str + newline).getBytes();
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void addToList(String str)
    {
        ListMeasure list  = new ListMeasure(String.valueOf(measureList.size()),str,"");
        measureList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    private void clean_list()
    {
        tmp_list.setId("");
        tmp_list.setDis("");
        tmp_list.setTime("");
        isGetTime = false;
    }

    private ListMeasure tmp_list = new ListMeasure("","","");
    private boolean isGetTime = false;
    private void receive(byte[] data)
    {
        String msg = new String(data);
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
        }

        String debug = TextUtil.toHexString(data) ;
        DataLog.e("debug:" + debug);
        receiveText.append(TextUtil.toCaretString(debug, newline.length() != 0));

        DataLog.e("status:" + status_mode);
        if(status_mode == 6 || status_mode == 7)
        {
            // get pahse data
            String header = Character.toString(debug.charAt(0)) +   Character.toString(debug.charAt(1));
            if(header.equals("FA"))
            {
                int len = data[1] << 8 | data[2] ;
                DataLog.e("len:" + len);

                if(len == 5)
                {
                    try
                    {
                        long temp =(data[4] << 8) & 0xFF00;
                        temp += data[3] & 0xFF;
                        DataLog.e( "temp:" + temp);

                        String t_org = Long.toString(temp);
                        double tempture = Double.valueOf(t_org);

                        String ttt = "--> t:" + Double.valueOf(tempture)+ "\n";
                        receiveText.append(ttt);

                        DataLog.e("id:" + tmp_list.getId());
                        DataLog.e("dis:" + tmp_list.getDis());

                        ListMeasure list = new ListMeasure(tmp_list.getId(),tmp_list.getDis(),String.format("%1.0f",Double.valueOf(tempture)));
                        measureList.add(list);
                        listAdapter.notifyDataSetChanged();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        DataLog.e("exception:" + e.toString());

                        ListMeasure list = new ListMeasure(tmp_list.getId(),tmp_list.getDis(),"ERR");
                        measureList.add(list);
                        listAdapter.notifyDataSetChanged();

                        btnOn.setEnabled(true);
                        btnOff.setEnabled(true);
                        btnCal.setEnabled(true);
                        BtnShots.setEnabled(true);
                        btnOn.setText("Laser On");
                        status_mode = 0;
                    }
                }
            }
            else if(header.equals("0E"))
            {
                String err_code = Character.toString(debug.charAt(1));
                if(err_code.equals("82"))
                {
                    status_mode = 5;
                    sendStrByte(uartCmd.enter_debug_mode);
                }

                ListMeasure list = new ListMeasure(tmp_list.getId(),tmp_list.getDis(),"ERR");
                measureList.add(list);
                listAdapter.notifyDataSetChanged();
            }

            if(status_mode == 6)
            {
                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
                btnCal.setEnabled(true);
                BtnShots.setEnabled(true);
                btnOn.setText("Laser On");
                status_mode = 0;
            }
            else
            {
                 cnt++;
                if(cnt < CNT_MAX)
                {
                    status_mode = 3;
                    sendStrByte(uartCmd.get_multi_distance);
                }
                else
                {
                    sendStrByte(uartCmd.get_single_distance);
                    cnt = 0;
                    status_mode = 0;

                    btnOn.setEnabled(true);
                    btnOff.setEnabled(true);
                    btnCal.setEnabled(true);
                    BtnShots.setEnabled(true);
                    btnOn.setText("Laser On");
                }
            }
        }
        else if(status_mode == 2 || status_mode == 3 )
        {
            DataLog.e("Data: ------"+ data[0]);
            String header = Character.toString(debug.charAt(0)) +   Character.toString(debug.charAt(1));
            if(header.equals("FA"))
            {
                int len = data[1] << 8 | data[2] ;
                DataLog.e("len:" + len);

                try
                {
                    long dis_mm = (((data[6] << 8)<<8)<<8) & 0xFF000000;
                    dis_mm +=((data[5] << 8)<<8) & 0xFF0000;
                    dis_mm += (data[4] << 8)& 0xFF00;
                    dis_mm += data[3] & 0xFF;
                    DataLog.e("dis_mm:" + dis_mm);

                    String d_org = Long.toString(dis_mm);
                    double off =  (double)offset/10;
                    double d = Double.valueOf(d_org)/10  + off;
                    double tmp = 0;
                    double a1= 0,b1= 0,c1= 0,d1= 0,e1= 0;
                    double a2= 0,b2= 0,c2= 0,d2= 0,e2= 0;

                    String ttt = "--> d:" + String.format("%.1f",Double.valueOf(d))+ "\n";
                    receiveText.append(ttt);
                    if(spinner.getSelectedItemPosition() == 1)
                    {
                        a1 = 1.875E-08  ;
                        b1 = -0.00000861;
                        c1 = 0.001379   ;
                        d1 = -0.08822   ;
                        e1 = -0.04327   ;
                        a2 = 0          ;
                        b2 = -9.801E-10 ;
                        c2 = 0.000001012;
                        d2 = 0.004274   ;
                        e2 = -2.13      ;
                    }
                    else if(spinner.getSelectedItemPosition() == 2)
                    {
                        a1 = 4.217E-08  ;
                        b1 = -0.00001878;
                        c1 = 0.002805   ;
                        d1 = -0.1557    ;
                        e1 = 0.436      ;
                        a2 = -3.89E-12  ;
                        b2 = 1.879E-08  ;
                        c2 = -0.00003245;
                        d2 = 0.02291    ;
                        e2 = -4.816     ;
                    }
                    else if(spinner.getSelectedItemPosition() == 3)
                    {
                        a1 = 3.369E-09   ;
                        b1 = -0.000001915;
                        c1 = 0.000412    ;
                        d1 = -0.03668    ;
                        e1 = 0.01279     ;
                        a2 = -2.425E-12  ;
                        b2 = 1.039E-08   ;
                        c2 = -0.00001674 ;
                        d2 = 0.0127      ;
                        e2 = -2.653      ;
                    }

                    if(d <= 200)
                    {
                        tmp = (a1 * Math.pow(d,4)) + (b1 * Math.pow(d,3)) + (c1 * Math.pow(d,2)) +
                                (d1 * d) + e1;
                    }
                    else
                    {
                        tmp = (a2 * Math.pow(d,4)) + (b2 * Math.pow(d,3)) + (c2 * Math.pow(d,2)) +
                                (d2 * d) + e2;
                    }

                    // d += offset;

                    double dis = d - tmp ;

                    ttt = "temp:" + Double.toString(tmp) + "\n";
                    receiveText.append(ttt);

                    ttt = "offset:" + Double.toString(off) + "\n";
                    receiveText.append(ttt);

                    String str_dis = String.format("%.1f",Double.valueOf(dis)) ;//String.valueOf(dis);
               /*
                    ListMeasure list  = new ListMeasure(String.valueOf(measureList.size()),str_dis,str_time);
                    measureList.add(list);
                    listAdapter.notifyDataSetChanged();
                */
                    tmp_list.setId(String.valueOf(measureList.size()));
                    tmp_list.setDis(str_dis);

                    if(status_mode == 3)
                        status_mode = 7;
                    else
                        status_mode = 6;

                    sendStrByte(uartCmd.get_tempture);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    DataLog.e("exception:" + e.toString());
                }
            }
            else
            {
                if(header.equals("0E"))
                {
                    int err = data[1] & 0xFF;
                    switch (err)
                    {
                        case 131: // 0x83
                            addToList("distance out of range");
                            break;
                        case 132: // 0x84
                            addToList("cmd not found");
                            break;
                        case 133: // 0x85
                            addToList("laser not enable");
                            break;
                        case 134: // 0x86
                            addToList("device params not found");
                            break;
                        case 135: // 0x87
                            addToList("wait for cmd repeat");
                            break;
                        case 136: // 0x88
                            addToList("ID exist");
                            break;
                        case 137: // 0x89
                            addToList("Low SNR");
                            break;
                    }

                    if(status_mode == 0 || status_mode == 2)
                    {
                        btnOn.setEnabled(true);
                        btnOff.setEnabled(true);
                        btnCal.setEnabled(true);
                        BtnShots.setEnabled(true);
                        btnOn.setText("Laser On");
                        status_mode = 0;
                    }

                    if(status_mode == 3)
                    {
                        cnt++;

                        if(cnt < CNT_MAX)
                        {
                            //send("O");
                            sendStrByte(uartCmd.get_multi_distance);
                        }
                        else
                        {
                            //send("D");
                            sendStrByte(uartCmd.get_single_distance);
                            cnt = 0;
                            status_mode = 0;

                            btnOn.setEnabled(true);
                            btnOff.setEnabled(true);
                            btnCal.setEnabled(true);
                            BtnShots.setEnabled(true);
                            btnOn.setText("Laser On");
                        }
                    }

                }
            }
        }
        else if(status_mode == 6)
        {
            //sendByte(DEBUG_MODE);
            sendStrByte(uartCmd.get_version);
            status_mode = 5;
        }
        else if(status_mode == 5)
        {
            DataLog.e("mode = 5, len:" + String.valueOf(data[2]));
            if(data[2] == 0x07)
            {
                byte[] tmp = {data[5]};
                String str_ver = String.valueOf(data[3]) + "." + String.valueOf(data[4]) +  ConversionUtils.bytesToString(tmp)
                        + "-20" + String.valueOf(data[6])
                        + "/" + String.valueOf(data[7])
                        + "/" + String.valueOf(data[8]);

                tv_status.setText("Connected");
                tv_ver.setText(str_ver);
                status_mode = 0;
                tv_status.setBackgroundColor(getResources().getColor(R.color.colorRecieveText));
            }
            else if(data[2] == 0x01)
            {
                //status_mode = 0;
               // sendStrByte(uartCmd.get_version);
                DataLog.e("Enter debug mode");
            }
        }
        else if(status_mode == 0 || status_mode == 2)
        {
            if(debug.contains("FA 00 01 01"))
            {
                btnOn.setEnabled(true);
                btnOff.setEnabled(true);
                btnCal.setEnabled(true);
                BtnShots.setEnabled(true);
                btnOn.setText("Laser On");
                status_mode = 0;
            }
        }
        else if(status_mode == 4)
        {
            status_mode = 0;
            // Calibration done.
            btnOn.setEnabled(true);
            btnOff.setEnabled(true);
            btnCal.setEnabled(true);
            BtnShots.setEnabled(true);
        }
    }

    void detected (String str)
    {
 //       tv_status.setText(str);
 //       tv_status.setBackgroundColor(getResources().getColor(R.color.colorRecieveText));
        DataLog.e("detected -- string");
        sendStrByte(uartCmd.enter_debug_mode);
        status_mode = 5;
    }

    void lost (String str)
    {
        tv_status.setText(str);
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
    }

    void status(String str) {
        tv_status.setText(str);
        DataLog.e("" + str);
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
        tv_ver.setText("");
        tv_status.setBackgroundColor(getResources().getColor(R.color.RedColor));
    }

    //    private byte[] DEBUG_MODE = {(byte)0xCD,(byte)0x05,(byte)0x3C,(byte)0xA0,(byte)0xE1,(byte)0x0D,(byte)0x0A};
    @Override
    public void onSerialConnect() {
 //       status("Connected");

        DataLog.e("onSerialConnect ---");

        connected = TestScreen.Connected.True;
//        tv_status.setBackgroundColor(getResources().getColor(R.color.colorRecieveText));

        //sendStrByte(uartCmd.get_version);
        sendStrByte(uartCmd.enter_debug_mode);
        status_mode = 5;
        timerHandler.postDelayed(timerRunnable, 100);
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            DataLog.e("get version");
            sendStrByte(uartCmd.get_version);
        }
    };

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