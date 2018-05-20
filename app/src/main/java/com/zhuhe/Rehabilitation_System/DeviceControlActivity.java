/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhuhe.Rehabilitation_System;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.EditText;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private BluetoothLeService mBluetoothLeService1;
    private BluetoothLeService mBluetoothLeService2;
    private BluetoothLeService mBluetoothLeService3;
    private BluetoothLeService mBluetoothLeService4;
    private BluetoothLeService mBluetoothLeService5;
    public boolean Recive_Speed_Set1=false;
    public boolean Recive_Speed_Set2=false;
    public boolean Recive_Speed_Set3=false;
    public boolean Recive_Speed_Set4=false;
    public boolean Recive_Speed_Set5=false;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected1 = false;
    private boolean mConnected2 = false;
    private boolean mConnected3 = false;
    private boolean mConnected4 = false;
    private boolean mConnected5 = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private LineChartView linerChart;  //定义折线图控件
    private boolean isonRecoding = false;
    private final int MAXDATACOUNT = 10000000;
    private Queue<SensorVector> dataCache1 = new ArrayDeque<>(MAXDATACOUNT);
    private Queue<SensorVector> dataCache2 = new ArrayDeque<>(MAXDATACOUNT);
    private Queue<SensorVector> dataCache3 = new ArrayDeque<>(MAXDATACOUNT);
    private Queue<SensorVector> dataCache4 = new ArrayDeque<>(MAXDATACOUNT);
    private Queue<SensorVector> dataCache5 = new ArrayDeque<>(MAXDATACOUNT);
    public  List<Double> feature1 = new ArrayList<Double>();
    public  List<Double> feature2 = new ArrayList<Double>();
    public  List<Double> feature3 = new ArrayList<Double>();
    public  List<Double> feature4 = new ArrayList<Double>();
    public  List<Double> feature5 = new ArrayList<Double>();
    public  List<Double> feature6 = new ArrayList<Double>();
    public  List<Double> feature7 = new ArrayList<Double>();
    public  List<Double> feature1_sta = new ArrayList<Double>();
    public  List<Double> feature2_sta = new ArrayList<Double>();
    public  List<Double> feature3_sta = new ArrayList<Double>();
    public  List<Double> feature4_sta = new ArrayList<Double>();
    public  List<Double> feature5_sta = new ArrayList<Double>();
    public  List<Double> feature6_sta = new ArrayList<Double>();
    public  List<Double> feature7_sta = new ArrayList<Double>();
    public  List Slice_static = new ArrayList();
    public  double [] feature1_Std={0,0.0549,0.0879,0.1538,0.2308,0.3846,0.4725,0.5385,0.5714,0.6374,0.6923,0.7363,0.8352,1.0000,1.0000,0.8352,0.7363,0.5165,0.4835,0.5604,0.6484,0.7912,0.8681,0.8571,0.7473,0.5055,0.3407,0.1758,0.0879,0.0659};
    //Sensor1_Acc_y
    public  double [] feature2_Std={0,0.0307,0.0813,0.1246,0.1337,0.1398,0.1744,0.2071,0.2612,0.3140,0.3947,0.4340,0.4798,0.4856,0.5070,0.5347,0.5863,0.6624,0.6468,0.5197,0.5504,0.5886,0.5854,0.5828,0.5966,0.6119,0.4541,0.4999,0.6072,0.6819,0.7377,0.7675,0.8408,0.8941,0.9230,0.9368,1.0000,0.9867,0.9744,0.9354,0.9101,0.8456,0.7831,0.7523,0.7489,0.7463,0.7180,0.6943,0.6685,0.6000,0.5180,0.4380,0.2835,0.1605,0.0431,0.0151};
    //Sensor2_Agl_x
    public  double [] feature3_Std={0.0955,0.1149,0.1504,0.1840,0.2199,0.2591,0.3112,0.3719,0.4370,0.5027,0.6387,0.7009,0.7593,0.8108,0.8569,0.8965,0.9266,0.9489,0.9675,0.9837,0.9944,1.0000,0.9994,0.9854,0.9564,0.9126,0.8425,0.7553,0.6546,0.5567,0.4633,0.3807,0.3062,0.2349,0.1667,0.1108,0.0647,0.0265,0.0063,0,0.0077,0.0200,0.0346,0.0563,0.0939,0.1377,0.1594,0.1494,0.1310,0.1062,0.0957,0.0905,0.1137,0.1621,0.1642,0.1510};
    //Sensor2_pitch
    public  double [] feature4_Std={0,0.0435,0.0891,0.2016,0.3471,0.6851,0.7907,0.7908,0.7614,0.8956,0.9547,1.0000,0.9622,0.8673,0.8133,0.7968,0.7914,0.7424,0.7075,0.6936,0.6914,0.6849,0.6872,0.7013,0.7135,0.7335,0.7447,0.7625,0.7700,0.7775,0.7917,0.8056,0.8194,0.8432,0.8601,0.8908,0.9121,0.9501,0.9780,0.9926,0.9435,0.8634,0.8216,0.7454,0.6623,0.5467,0.4865,0.4095,0.3544,0.2796,0.2352,0.1421,0.0940,0.0613,0.0154};
    //Sensor5_Agl_x
    public  double [] feature5_Std={0.0028,0.0240,0.0606,0.0793,0.0932,0.1288,0.1782,0.2387,0.2942,0.3479,0.3959,0.4244,0.4443,0.4610,0.4776,0.4871,0.4925,0.4976,0.5036,0.5107,0.5193,0.5299,0.5426,0.5584,0.5752,0.5927,0.6111,0.6313,0.6530,0.6770,0.7039,0.7343,0.7658,0.8029,0.8462,0.8946,0.9326,0.9632,0.9908,1.0000,0.9958,0.9770,0.9514,0.9140,0.8671,0.8083,0.7418,0.5860,0.5017,0.4132,0.3194,0.2268,0.1409,0.0669,0.0210, 0};
    //Sensor5_pitch
    public  double [] feature6_Std={0,0.0225,0.0654,0.1031,0.1436,0.1833,0.2308,0.2753,0.3149,0.3557,0.4187,0.4380,0.4547,0.4698,0.4818,0.4877,0.4912,0.4993,0.5127,0.5224,0.5339,0.5466,0.5648,0.6362,0.6657,0.6980,0.7329,0.8200,0.9562,0.9864,1.0000,0.9966,0.8752,0.6089,0.4454,0.3571,0.2662,0.1804,0.1076,0.0456,0.0112};
    //Sensor4_pitch
    public  double [] feature7_Std={0.0377,0.0954,0.2050,0.2639,0.3749,0.4345,0.4941,0.5610,0.6268,0.6892,0.7469,0.8011,0.8499,0.8845,0.9105,0.9262,0.9416,0.9557,0.9670,0.9757,0.9856,0.9958,1.0000,0.9882,0.9651,0.9239,0.7726,0.6951,0.5533,0.4661,0.3744,0.3033,0.2453,0.1979,0.1576,0.1166,0.0763,0.0382,0.0120,0,0.0111,0.0344,0.0646,0.0917,0.0990,0.0813,0.0624};
    //Sensor3_pitch
    //public  double [] feature5_feedback_std={71.4100,72.9900,75.7200,77.1200,78.1600,80.8100,84.5000,89.0200,93.1600,97.1700,100.7500,102.8800,104.3600,105.6100,106.8500,107.5600,107.9600,108.3400,108.7900,109.3200,109.9600,110.7500,111.7000,112.8800,114.1300,115.4400,116.8100,118.3200,119.9400,121.7300,123.7400,126.0100,128.3600,131.1300,134.3600,137.9700,140.8100,143.0900,145.1500,145.8400,145.5300,144.1200,142.2100,139.4200,135.9200,131.5300,126.5700,114.9400,108.6500,102.0400,95.0400,88.1300,81.7200,76.1900,72.7700,71.2000};
    public int feature_index[][]=new int[7][2];
    //    public double feature1[]=new double[MAXDATACOUNT];//Sensor1_Acc_y
//    public double feature2[]=new double[MAXDATACOUNT];//Sensor2_Agl_x
//    public double feature3[]=new double[MAXDATACOUNT];//Sensor2_pitch
//    public double feature4[]=new double[MAXDATACOUNT];//Sensor5_Agl_x
//    public double feature5[]=new double[MAXDATACOUNT];//Sensor5_pitch
//   public double feature1[],feature2[],feature3[],feature4[],feature5[];
    private Queue<dataVector> chartDataQueue = new ArrayDeque<>(10);
    private TextView pitchInfo,yawInfo,rollInfo;//定义显示控件
    private String smsg = "";    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存
    public float pi= (float) 3.14159;//定义pi
    public String filename=""; //用来保存存储的文件名
    public String s;
    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;
    public String senddata;
    int feedback_temp=0;
    boolean Isfeedback=false;
    boolean Upfeedback=false;
    boolean Downfeedback=false;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
    /******************人物模型共用绘图变量*******************/
    public int Body_weight=60;
    public int Limb_weight=20;
    /******************侧面图所用变量*************************/
    public float Thigh=90;//大腿长度
    public float Crus=110;//小腿长度
    public float Joint=5;//关节大小
    public float Postbrachium=90;//大臂长度
    public float Forearm=110;//小臂长度
    public float Hip_L=-10;//左大腿角度
    public float Hip_R=10;//右大腿角度
    public float Knee_L=0;//左膝角度
    public float Knee_R=0;//右膝角度
    public float Head_x=50;//头部横坐标
    public float Head_y=60;//头部纵坐标
    public float Head_size=50;//头部大小
    public float Body=160;//身体大小
    public float Arm_L=110;//左大臂角度
    public float Arm_R=80;//右大臂角度
    public float Hand_L=110;//左小臂角度
    public float Hand_R=110;//右小臂角度
    /******************正面图所用变量*************************/
    public float Thigh2=90;//大腿长度
    public float Crus2=110;//小腿长度
    public float Joint2=5;//关节大小
    public float Postbrachium2=90;
    public float Forearm2=110;
    public float Hip_L2=-10;
    public float Hip_R2=10;
    public float Knee_L2=5;
    public float Knee_R2=-5;
    public float Head_x2=500;
    public float Head_y2=60;
    public float Head_size2=50;
    public float Body2=160;
    public float Arm_L2=-60;
    public float Arm_R2=60;
    public float Hand_L2=10;
    public float Hand_R2=-10;
    /******************数据传输相关变量*************************/
    public float Hip_L_temp=0,Hip_R_temp=0,Knee_L_temp=0,Knee_R_temp=0,Hip_L2_temp=0,Hip_R2_temp=0,Knee_L2_temp=0,Knee_R2_temp=0;
    public final static String SensorOne  = "D1:FB:17:39:B5:5F";
    public final static String SensorTwo  = "C3:99:F4:D8:19:18";
    public final static String SensorThree  = "C1:23:41:C3:43:E0";
    public final static String SensorFour  = "E8:1C:46:BD:44:C1";
    //public final static String SensorFive  = "C3:A2:70:70:93:34";
   // public final static String SensorFive  = "D4:8E:96:16:9C:2E";
    public final static String SensorFive  = "EC:58:2E:8D:30:A2";
    public int NetCount=7;//定义传感器节点个数
    public double Bt_Acc_x[]=new double[NetCount];//保存蓝牙传输的pitch角度
    public double Bt_Acc_y[]=new double[NetCount];//保存蓝牙传输的yaw角度
    public double Bt_Acc_z[]=new double[NetCount];//保存蓝牙传输的roll角度
    public double Bt_Agl_x[]=new double[NetCount];//保存蓝牙传输的pitch角度
    public double Bt_Agl_y[]=new double[NetCount];//保存蓝牙传输的yaw角度
    public double Bt_Agl_z[]=new double[NetCount];//保存蓝牙传输的roll角度
    public double Bt_pitch[]=new double[NetCount];//保存蓝牙传输的pitch角度
    public double Bt_yaw[]=new double[NetCount];//保存蓝牙传输的yaw角度
    public double Bt_roll[]=new double[NetCount];//保存蓝牙传输的roll角度
    public int DataCount=NetCount*3+2;//数据包包含数据个数（首部+数据+尾部）
    public double temp_t[]=new double[DataCount];//接收数据缓存
    public int Number=0;//接收数据编号
    private final String FILENAME = ".csv";
    public double test[][]=new double[9][3];//曲线图更新对象数据缓存

    public String temp="";
    public boolean Sync_flag=false;//首部同步信号
    public int Correct=0;//首部修正量
    public int Sensor_Number=0;//对应传感器编号
    public byte Send_Speed=0x08;//蓝牙回传速率
    //public boolean Sync_end=false;//尾部同步信号
    //public int Accept_Number=-1;
    //public boolean Trans_Complete=true;
    public Button btn_record;
    public Button btn_analy;
    public Button btn_last,btn_next;
    public Button btn_feedback;
    public TextView feature_notice;
    public int Serial_Number=0;
    public String trush;
    MyView myView;
    //byte[] WriteBytes = null;
    byte[] WriteBytes = new byte[20];
    private BluetoothAdapter mBluetoothAdapter;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService1 = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService2 = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService3 = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService4 = ((BluetoothLeService.LocalBinder) service).getService();
            mBluetoothLeService5 = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService1.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            if (!mBluetoothLeService2.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            if (!mBluetoothLeService3.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            if (!mBluetoothLeService4.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            if (!mBluetoothLeService5.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            if(!mConnected5)
            {
                mBluetoothLeService5.connect(SensorFive);
            }
           else if(!mConnected4 && mConnected5)
            {
                mBluetoothLeService4.connect(SensorFour);
            }
            else if(!mConnected3 && mConnected4 &&mConnected5)
            {
                mBluetoothLeService3.connect(SensorThree);
            }
            else if(!mConnected2 && mConnected3 && mConnected4 && mConnected5)
            {
                mBluetoothLeService2.connect(SensorTwo);
            }
            else if(!mConnected1&& mConnected2 && mConnected3 && mConnected4 && mConnected5 )
            {
                mBluetoothLeService1.connect(SensorOne);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService1 = null;
            mBluetoothLeService2 = null;
            mBluetoothLeService3 = null;
            mBluetoothLeService4 = null;
            mBluetoothLeService5 = null;
        }
    };


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action1 = intent.getAction();
            if (action1.equals(SensorOne+BluetoothLeService.ACTION_GATT_CONNECTED)) {
                //mConnected = true;
            } else if (action1.equals(SensorOne+BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                mConnected1 = false;
            } else if (action1.equals(SensorOne+BluetoothLeService.ACTION_DATA_AVAILABLE1)) {
                mConnected1 = true;
                String data1 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA1);
                if (data1 != null) {
                    Number=0;
                    StringTokenizer st1 = new StringTokenizer(data1, ",");//将接收到的字符串进行分割
                    while (st1.hasMoreTokens()) {
                        trush=st1.nextToken();
                        Bt_Acc_x[0]= Double.valueOf(st1.nextToken());
                        Bt_Acc_y[0]= Double.valueOf(st1.nextToken());
                        Bt_Acc_z[0]= Double.valueOf(st1.nextToken());
                        Bt_Agl_x[0]= Double.valueOf(st1.nextToken());
                        Bt_Agl_y[0]= Double.valueOf(st1.nextToken());
                        Bt_Agl_z[0]= Double.valueOf(st1.nextToken());
                        Bt_pitch[0]= Double.valueOf(st1.nextToken());
                        Bt_yaw[0]= Double.valueOf(st1.nextToken());
                        Bt_roll[0]= Double.valueOf(st1.nextToken());
                        test[Number][0]=Bt_pitch[0];
                        test[Number][1]=Bt_yaw[0];
                        test[Number][2]=Bt_roll[0];
                    }
                    if(mConnected1 && mConnected2 && mConnected3 && mConnected4 && mConnected5 && isonRecoding)
                    {
                        feature1.add(Bt_Acc_y[0]);
                        dataCache1.add(new SensorVector(Bt_Acc_x[0], Bt_Acc_y[0], Bt_Acc_z[0],Bt_Agl_x[0], Bt_Agl_y[0], Bt_Agl_z[0],Bt_pitch[0],Bt_yaw[0], Bt_roll[0]));
                        if (dataCache1.size() == MAXDATACOUNT) dataCache1.remove();
                    }
                    displayData();
                    //mBluetoothLeService.writeByes(new byte[]{'w', 't', 'z', 'n'});
                    if(!Recive_Speed_Set1)
                    {
                        mBluetoothLeService1.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x03,(byte)Send_Speed,(byte)0x00});//发送指令
                        Recive_Speed_Set1=true;
                    }
                    //mBluetoothLeService.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x22,(byte)0x01,(byte)0x00});//发送休眠指令
                }
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action2 = intent.getAction();
           if (action2.equals(SensorTwo+BluetoothLeService.ACTION_GATT_CONNECTED)) {
               // mConnected2 = true;
            } else if (action2.equals(SensorTwo+BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                mConnected2 = false;
            }  else if (action2.equals(SensorTwo+BluetoothLeService.ACTION_DATA_AVAILABLE2 )) {
               mConnected2 = true;
               String data2 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA2);
               if (data2 != null) {
                   Number=1;
                   StringTokenizer st2 = new StringTokenizer(data2, ",");//将接收到的字符串进行分割
                   while (st2.hasMoreTokens()) {
                       trush=st2.nextToken();
                       Bt_Acc_x[1]= Double.valueOf(st2.nextToken());
                       Bt_Acc_y[1]= Double.valueOf(st2.nextToken());
                       Bt_Acc_z[1]= Double.valueOf(st2.nextToken());
                       Bt_Agl_x[1]= Double.valueOf(st2.nextToken());
                       Bt_Agl_y[1]= Double.valueOf(st2.nextToken());
                       Bt_Agl_z[1]= Double.valueOf(st2.nextToken());
                       Bt_pitch[1]= Double.valueOf(st2.nextToken());
                       Bt_yaw[1]= Double.valueOf(st2.nextToken());
                       Bt_roll[1]= Double.valueOf(st2.nextToken());
                       test[Number][0]=Bt_pitch[1];
                       test[Number][1]=Bt_yaw[1];
                       test[Number][2]=Bt_roll[1];
                   }
                   if(mConnected1 && mConnected2 && mConnected3 && mConnected4 && mConnected5 && isonRecoding)
                   {
                       feature2.add(Bt_Agl_x[1]);
                       feature3.add(Bt_pitch[1]);
                       dataCache2.add(new SensorVector(Bt_Acc_x[1], Bt_Acc_y[1], Bt_Acc_z[1],Bt_Agl_x[1], Bt_Agl_y[1], Bt_Agl_z[1],Bt_pitch[1],Bt_yaw[1], Bt_roll[1]));
                       if (dataCache2.size() == MAXDATACOUNT) dataCache2.remove();
                   }
                   displayData();
                   //mBluetoothLeService2.writeByes(new byte[]{'w', 't', 'z', 'n'});
                   if(!Recive_Speed_Set2)
                   {
                       mBluetoothLeService2.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x03,(byte)Send_Speed,(byte)0x00});//发送指令
                       Recive_Speed_Set2=true;
                   }
               }
           }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action3 = intent.getAction();
            if (action3.equals(SensorThree + BluetoothLeService.ACTION_GATT_CONNECTED)) {
               // mConnected3 = true;
            } else if (action3.equals(SensorThree + BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                mConnected3 = false;
            } else if (action3.equals(SensorThree + BluetoothLeService.ACTION_DATA_AVAILABLE3)) {
                String data3 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA3);
                mConnected3 = true;
                if (data3 != null) {
                    Number=2;
                    StringTokenizer st3 = new StringTokenizer(data3, ",");//将接收到的字符串进行分割
                    while (st3.hasMoreTokens()) {
                        trush=st3.nextToken();
                        Bt_Acc_x[2]= Double.valueOf(st3.nextToken());
                        Bt_Acc_y[2]= Double.valueOf(st3.nextToken());
                        Bt_Acc_z[2]= Double.valueOf(st3.nextToken());
                        Bt_Agl_x[2]= Double.valueOf(st3.nextToken());
                        Bt_Agl_y[2]= Double.valueOf(st3.nextToken());
                        Bt_Agl_z[2]= Double.valueOf(st3.nextToken());
                        Bt_pitch[2]= Double.valueOf(st3.nextToken());
                        Bt_yaw[2]= Double.valueOf(st3.nextToken());
                        Bt_roll[2]= Double.valueOf(st3.nextToken());
                        test[Number][0]=Bt_pitch[2];
                        test[Number][1]=Bt_yaw[2];
                        test[Number][2]=Bt_roll[2];
                    }
                    if(mConnected1 && mConnected2 && mConnected3 && mConnected1 && mConnected5 && isonRecoding)
                    {
                        feature7.add(Bt_pitch[2]);
                        dataCache3.add(new SensorVector(Bt_Acc_x[2], Bt_Acc_y[2], Bt_Acc_z[2],Bt_Agl_x[2], Bt_Agl_y[2], Bt_Agl_z[2],Bt_pitch[2],Bt_yaw[2], Bt_roll[2]));
                        if (dataCache3.size() == MAXDATACOUNT) dataCache3.remove();
                    }
                    displayData();
                   // mBluetoothLeService3.writeByes(new byte[]{'w', 't', 'z', 'n'});
                    if(!Recive_Speed_Set3)
                    {
                        mBluetoothLeService3.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x03,(byte)Send_Speed,(byte)0x00});//发送指令
                        Recive_Speed_Set3=true;
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action4 = intent.getAction();
            if (action4.equals(SensorFour+BluetoothLeService.ACTION_GATT_CONNECTED)) {
                //mConnected = true;
            } else if (action4.equals(SensorFour+BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                mConnected4 = false;
            } else if (action4.equals(SensorFour+BluetoothLeService.ACTION_DATA_AVAILABLE4)) {
                mConnected4 = true;
                String data4 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA4);
                if (data4 != null) {
                    Number=3;
                    StringTokenizer st4 = new StringTokenizer(data4, ",");//将接收到的字符串进行分割
                    while (st4.hasMoreTokens()) {
                        trush=st4.nextToken();
                        Bt_Acc_x[3]= Double.valueOf(st4.nextToken());
                        Bt_Acc_y[3]= Double.valueOf(st4.nextToken());
                        Bt_Acc_z[3]= Double.valueOf(st4.nextToken());
                        Bt_Agl_x[3]= Double.valueOf(st4.nextToken());
                        Bt_Agl_y[3]= Double.valueOf(st4.nextToken());
                        Bt_Agl_z[3]= Double.valueOf(st4.nextToken());
                        Bt_pitch[3]= Double.valueOf(st4.nextToken());
                        Bt_yaw[3]= Double.valueOf(st4.nextToken());
                        Bt_roll[3]= Double.valueOf(st4.nextToken());
                        test[Number][0]=Bt_pitch[3];
                        test[Number][1]=Bt_yaw[3];
                        test[Number][2]=Bt_roll[3];
                    }
                    if(mConnected1 && mConnected2 && mConnected3 && mConnected4 && mConnected5 && isonRecoding)
                    {
                        feature6.add(Bt_pitch[3]);
                        dataCache4.add(new SensorVector(Bt_Acc_x[3], Bt_Acc_y[3], Bt_Acc_z[3],Bt_Agl_x[3], Bt_Agl_y[3], Bt_Agl_z[3],Bt_pitch[3],Bt_yaw[3], Bt_roll[3]));
                        if (dataCache4.size() == MAXDATACOUNT) dataCache4.remove();
                    }
                    displayData();
                    //mBluetoothLeService.writeByes(new byte[]{'w', 't', 'z', 'n'});
                    if(!Recive_Speed_Set4)
                    {
                        mBluetoothLeService4.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x03,(byte)Send_Speed,(byte)0x00});//发送指令
                        Recive_Speed_Set4=true;
                    }
                    //mBluetoothLeService.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x22,(byte)0x01,(byte)0x00});//发送休眠指令
                }
            }
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action5 = intent.getAction();
            if (action5.equals(SensorFive+BluetoothLeService.ACTION_GATT_CONNECTED)) {
                //mConnected = true;
            } else if (action5.equals(SensorFive+BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                mConnected5 = false;
            } else if (action5.equals(SensorFive+BluetoothLeService.ACTION_DATA_AVAILABLE5)) {
                mConnected5 = true;
                String data5 = intent.getStringExtra(BluetoothLeService.EXTRA_DATA5);
                if (data5 != null) {
                    Number=4;
                    StringTokenizer st5 = new StringTokenizer(data5, ",");//将接收到的字符串进行分割
                    while (st5.hasMoreTokens()) {
                        trush=st5.nextToken();
                        Bt_Acc_x[4]= Double.valueOf(st5.nextToken());
                        Bt_Acc_y[4]= Double.valueOf(st5.nextToken());
                        Bt_Acc_z[4]= Double.valueOf(st5.nextToken());
                        Bt_Agl_x[4]= Double.valueOf(st5.nextToken());
                        Bt_Agl_y[4]= Double.valueOf(st5.nextToken());
                        Bt_Agl_z[4]= Double.valueOf(st5.nextToken());
                        Bt_pitch[4]= Double.valueOf(st5.nextToken());
                        Bt_yaw[4]= Double.valueOf(st5.nextToken());
                        Bt_roll[4]= Double.valueOf(st5.nextToken());
                        test[Number][0]=Bt_pitch[4];
                        test[Number][1]=Bt_yaw[4];
                        test[Number][2]=Bt_roll[4];
                    }
                    if(mConnected1 && mConnected2 && mConnected3 && mConnected4 && mConnected5 && isonRecoding)
                    {
                        feature4.add(Bt_Agl_x[4]);
                        feature5.add(Bt_pitch[4]);
                        dataCache5.add(new SensorVector(Bt_Acc_x[4], Bt_Acc_y[4], Bt_Acc_z[4],Bt_Agl_x[4], Bt_Agl_y[4], Bt_Agl_z[4],Bt_pitch[4],Bt_yaw[4], Bt_roll[4]));
                        if (dataCache5.size() == MAXDATACOUNT) dataCache5.remove();
                    }
                    displayData();
                    //mBluetoothLeService.writeByes(new byte[]{'w', 't', 'z', 'n'});
                    if(!Recive_Speed_Set5)
                    {
                        mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x03,(byte)Send_Speed,(byte)0x00});//发送指令
                        Recive_Speed_Set5=true;
                    }
                    if(Isfeedback)//是否开启了反馈
                    {
                        //先根据真实-预测决定开启正还是负反馈
                        //如需开启正反馈
                        if(Bt_pitch[4]>150)//150
                        {
                            if(!Upfeedback)//是否开启了正反馈
                            {
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x02,(byte)0x00});//端口0高电平
                                Upfeedback=true;//没开启则开启正反馈
                            }//如果已经开启了正反馈，则什么也不做
                            if(Upfeedback && Downfeedback)//如果开启正反馈时还有负反馈则关闭负反馈
                            {
                                Downfeedback=false;
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x10,(byte)0x03,(byte)0x00});//端口2低电平
                            }
                        }
                        else if((Bt_pitch[4]<60))//60
                        {
                            if(!Downfeedback)//是否开启了负反馈
                            {
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x10,(byte)0x02,(byte)0x00});//端口2高电平
                                Downfeedback=true;//没开启则开启负反馈
                            }//如果已经开启了负反馈，则什么也不做
                            if(Upfeedback && Downfeedback)//如果开启负反馈时还有正反馈则关闭正反馈
                            {
                                Upfeedback=false;
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x03,(byte)0x00});//端口0低电平
                            }
                        }
                        else
                        {//此次不需要反馈
                            if(Upfeedback)//关闭上次正反馈
                            {
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x03,(byte)0x00});//端口0低电平
                                Upfeedback=false;
                            }
                            if(Downfeedback)//关闭上次负反馈
                            {
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x10,(byte)0x03,(byte)0x00});//端口2低电平
                                Downfeedback=false;
                            }
                        }
                    }


                    /*
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x02,(byte)0x00});//端口0高电平
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x10,(byte)0x02,(byte)0x00});//端口2高电平
                                 mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x03,(byte)0x00});//端口0低电平
                                mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x10,(byte)0x03,(byte)0x00});//端口2低电平
                     */
                    //mBluetoothLeService.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x22,(byte)0x01,(byte)0x00});//发送休眠指令

                }
            }
        }
    };

// mBluetoothLeService.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x00,(byte)0x00,(byte)0x00});//发送指令



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        btn_record= (Button) findViewById(R.id.Button07);
        feature_notice= (TextView) findViewById(R.id.textView3);
        btn_analy= (Button) findViewById(R.id.button1);
        btn_analy.setOnClickListener(new onAnalyClicked());
        btn_last= (Button) findViewById(R.id.last);
        btn_last.setOnClickListener(new onlastClicked());
        btn_next= (Button) findViewById(R.id.next);
        btn_next.setOnClickListener(new onnextClicked());
        btn_feedback= (Button) findViewById(R.id.feedback);
        btn_feedback.setOnClickListener(new onfeedbackClicked());
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化Bluetooth adapter.BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查手机是否支持蓝牙功能
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
        getActionBar().setTitle("运动康复系统");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        linerChart = linerChartInit();  //初始化图表
        pitchInfo = (TextView)findViewById(R.id.pitchInfo);
        yawInfo = (TextView)findViewById(R.id.yawInfo);
        rollInfo = (TextView)findViewById(R.id.rollInfo);
        myView = new MyView(DeviceControlActivity.this);
        FrameLayout Human_model=(FrameLayout)findViewById(R.id.frameLayout1);//加载人物模型
        Human_model.addView(myView);
    }
    public class MyView extends View{//View构造人物模型

        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }


        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);//指定画布的背景颜色为白色
            Paint paint=new Paint();//创建一个采用默认设置的画笔
            paint.setColor(Color.RED);//设置画笔颜色
/**************************侧面图*************************/
            paint.setStrokeWidth(10);
            paint.setAntiAlias(true);//使用抗锯齿功能
            canvas.drawCircle(Head_x, Head_y, Head_size, paint);//头部
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Body_weight);
            canvas.drawLine(Head_x, Head_y+Head_size, Head_x, Head_y+Head_size+Body-Joint, paint);//躯干
            paint.setStrokeWidth(Limb_weight);
            canvas.drawCircle(Head_x, Head_y+Head_size+Body, Joint, paint);//髋关节
            canvas.drawLine(Head_x+Joint*(float)Math.sin((float)Hip_L*pi/180), Head_y+Head_size+Body+Joint*(float)Math.cos((float)Hip_L*pi/180), Head_x+Thigh*(float)Math.sin((float)Hip_L*pi/180), Head_y+Head_size+Body+Thigh*(float)Math.cos((float)Hip_L*pi/180), paint);//左大腿
            canvas.drawCircle(Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_L*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_L*pi/180), Joint, paint);//左膝关节
            canvas.drawLine(Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_L*pi/180)+Joint*(float)Math.sin((float)Knee_L*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_L*pi/180)+Joint*(float)Math.cos((float)Knee_L*pi/180),Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_L*pi/180)+(Joint+Crus)*(float)Math.sin((float)Knee_L*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_L*pi/180)+(Joint+Crus)*(float)Math.cos((float)Knee_L*pi/180), paint);//左小腿
            canvas.drawLine(Head_x+Joint*(float)Math.sin((float)Hip_R*pi/180), Head_y+Head_size+Body+Joint*(float)Math.cos((float)Hip_R*pi/180), Head_x+Thigh*(float)Math.sin((float)Hip_R*pi/180), Head_y+Head_size+Body+Thigh*(float)Math.cos((float)Hip_R*pi/180), paint);//右大腿
            canvas.drawCircle(Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_R*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_R*pi/180), Joint, paint);//右膝关节
            canvas.drawLine(Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_R*pi/180)+Joint*(float)Math.sin((float)Knee_R*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_R*pi/180)+Joint*(float)Math.cos((float)Knee_R*pi/180),Head_x+(Joint+Thigh)*(float)Math.sin((float)Hip_R*pi/180)+(Joint+Crus)*(float)Math.sin((float)Knee_R*pi/180), Head_y+Head_size+Body+(Joint+Thigh)*(float)Math.cos((float)Hip_R*pi/180)+(Joint+Crus)*(float)Math.cos((float)Knee_R*pi/180), paint);//右小腿
            canvas.drawLine(Head_x+Joint*(float)Math.sin((float)Arm_L*pi/180), Head_y+Head_size+Body/3+Joint*(float)Math.cos((float)Arm_L*pi/180), Head_x+Postbrachium*(float)Math.sin((float)Arm_L*pi/180), Head_y+Head_size+Body/3+Postbrachium*(float)Math.cos((float)Arm_L*pi/180), paint);//左大臂
            canvas.drawCircle(Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_L*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_L*pi/180), Joint, paint);//左肘关节
            canvas.drawLine(Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_L*pi/180)+Joint*(float)Math.sin((float)Hand_L*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_L*pi/180)+Joint*(float)Math.cos((float)Hand_L*pi/180),Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_L*pi/180)+(Joint+Forearm)*(float)Math.sin((float)Hand_L*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_L*pi/180)+(Joint+Forearm)*(float)Math.cos((float)Hand_L*pi/180), paint);//左小臂
            canvas.drawLine(Head_x+Joint*(float)Math.sin((float)Arm_R*pi/180), Head_y+Head_size+Body/3+Joint*(float)Math.cos((float)Arm_R*pi/180), Head_x+Postbrachium*(float)Math.sin((float)Arm_R*pi/180), Head_y+Head_size+Body/3+Postbrachium*(float)Math.cos((float)Arm_R*pi/180), paint);//右大臂
            canvas.drawCircle(Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_R*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_R*pi/180), Joint, paint);//右肘关节
            canvas.drawLine(Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_R*pi/180)+Joint*(float)Math.sin((float)Hand_R*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_R*pi/180)+Joint*(float)Math.cos((float)Hand_R*pi/180),Head_x+(Joint+Postbrachium)*(float)Math.sin((float)Arm_R*pi/180)+(Joint+Forearm)*(float)Math.sin((float)Hand_R*pi/180), Head_y+Head_size+Body/3+(Joint+Postbrachium)*(float)Math.cos((float)Arm_R*pi/180)+(Joint+Forearm)*(float)Math.cos((float)Hand_R*pi/180), paint);//右小臂
/******************正面图*************************/
            paint.setColor(Color.BLUE);//设置画笔颜色
            paint.setStrokeWidth(10);
            paint.setAntiAlias(true);//使用抗锯齿功能
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(Head_x2, Head_y2, Head_size2, paint);//头部
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Body_weight);
            canvas.drawLine(Head_x2, Head_y2+Head_size2, Head_x2, Head_y2+Head_size2+Body2-Joint2, paint);//躯干
            paint.setStrokeWidth(Limb_weight);
            canvas.drawCircle(Head_x2, Head_y2+Head_size2+Body2, Joint2, paint);//髋关节
            canvas.drawLine(Head_x2-Limb_weight+Joint2*(float)Math.sin((float)Hip_L2*pi/180), Head_y2+Head_size2+Body2+Joint2*(float)Math.cos((float)Hip_L2*pi/180), Head_x2-Limb_weight+Thigh2*(float)Math.sin((float)Hip_L2*pi/180), Head_y2+Head_size2+Body2+Thigh2*(float)Math.cos((float)Hip_L2*pi/180), paint);//左大腿
            canvas.drawCircle(Head_x2-Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_L2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_L2*pi/180), Joint2, paint);//左膝关节
            canvas.drawLine(Head_x2-Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_L2*pi/180)+Joint2*(float)Math.sin((float)Knee_L2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_L2*pi/180)+Joint2*(float)Math.cos((float)Knee_L2*pi/180),Head_x2-Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_L2*pi/180)+(Joint2+Crus2)*(float)Math.sin((float)Knee_L2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_L2*pi/180)+(Joint2+Crus2)*(float)Math.cos((float)Knee_L2*pi/180), paint);//左小腿
            canvas.drawLine(Head_x2+Limb_weight+Joint2*(float)Math.sin((float)Hip_R2*pi/180), Head_y2+Head_size2+Body2+Joint2*(float)Math.cos((float)Hip_R2*pi/180), Head_x2+Limb_weight+Thigh2*(float)Math.sin((float)Hip_R2*pi/180), Head_y2+Head_size2+Body2+Thigh2*(float)Math.cos((float)Hip_R2*pi/180), paint);//右大腿
            canvas.drawCircle(Head_x2+Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_R2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_R2*pi/180), Joint2, paint);//右膝关节
            canvas.drawLine(Head_x2+Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_R2*pi/180)+Joint2*(float)Math.sin((float)Knee_R2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_R2*pi/180)+Joint2*(float)Math.cos((float)Knee_R2*pi/180),Head_x2+Limb_weight+(Joint2+Thigh2)*(float)Math.sin((float)Hip_R2*pi/180)+(Joint2+Crus2)*(float)Math.sin((float)Knee_R2*pi/180), Head_y2+Head_size2+Body2+(Joint2+Thigh2)*(float)Math.cos((float)Hip_R2*pi/180)+(Joint2+Crus2)*(float)Math.cos((float)Knee_R2*pi/180), paint);//右小腿
            canvas.drawLine(Head_x2+Joint2*(float)Math.sin((float)Arm_L2*pi/180), Head_y2+Head_size2+Body2/3+Joint2*(float)Math.cos((float)Arm_L2*pi/180), Head_x2+Postbrachium2*(float)Math.sin((float)Arm_L2*pi/180), Head_y2+Head_size2+Body2/3+Postbrachium2*(float)Math.cos((float)Arm_L2*pi/180), paint);//左大臂
            canvas.drawCircle(Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_L2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_L2*pi/180), Joint2, paint);//左肘关节
            canvas.drawLine(Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_L2*pi/180)+Joint2*(float)Math.sin((float)Hand_L2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_L2*pi/180)+Joint2*(float)Math.cos((float)Hand_L2*pi/180),Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_L2*pi/180)+(Joint2+Forearm2)*(float)Math.sin((float)Hand_L2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_L2*pi/180)+(Joint2+Forearm2)*(float)Math.cos((float)Hand_L2*pi/180), paint);//左小臂
            canvas.drawLine(Head_x2+Joint2*(float)Math.sin((float)Arm_R2*pi/180), Head_y2+Head_size2+Body2/3+Joint2*(float)Math.cos((float)Arm_R2*pi/180), Head_x2+Postbrachium2*(float)Math.sin((float)Arm_R2*pi/180), Head_y2+Head_size2+Body2/3+Postbrachium2*(float)Math.cos((float)Arm_R2*pi/180), paint);//右大臂
            canvas.drawCircle(Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_R2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_R2*pi/180), Joint2, paint);//右肘关节
            canvas.drawLine(Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_R2*pi/180)+Joint2*(float)Math.sin((float)Hand_R2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_R2*pi/180)+Joint2*(float)Math.cos((float)Hand_R2*pi/180),Head_x2+(Joint2+Postbrachium2)*(float)Math.sin((float)Arm_R2*pi/180)+(Joint2+Forearm2)*(float)Math.sin((float)Hand_R2*pi/180), Head_y2+Head_size2+Body2/3+(Joint2+Postbrachium2)*(float)Math.cos((float)Arm_R2*pi/180)+(Joint2+Forearm2)*(float)Math.cos((float)Hand_R2*pi/180), paint);//右小臂

            super.onDraw(canvas);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver1, makeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver2, makeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver3, makeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver4, makeGattUpdateIntentFilter());
        registerReceiver(mGattUpdateReceiver5, makeGattUpdateIntentFilter());
        if (mBluetoothLeService5 != null) {
            final boolean result5 = mBluetoothLeService5.connect(SensorFive);
            Log.d(TAG, "Connect request result=" + result5);
        }

        if (mBluetoothLeService4 != null && mConnected5) {
            final boolean result4 = mBluetoothLeService4.connect(SensorFour);
            Log.d(TAG, "Connect request result=" + result4);
        }

        if (mBluetoothLeService3 != null && mConnected4 && mConnected5) {
            final boolean result3 = mBluetoothLeService3.connect(SensorThree);
            Log.d(TAG, "Connect request result=" + result3);
        }

        if (mBluetoothLeService2 != null && mConnected3 && mConnected4 && mConnected5) {
            final boolean result2 = mBluetoothLeService2.connect(SensorTwo);
            Log.d(TAG, "Connect request result=" + result2);
        }
        if (mBluetoothLeService1 != null && mConnected2 && mConnected3 && mConnected4 && mConnected5) {
            final boolean result1 = mBluetoothLeService1.connect(SensorOne);
            Log.d(TAG, "Connect request result=" + result1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver1);
        unregisterReceiver(mGattUpdateReceiver2);
        unregisterReceiver(mGattUpdateReceiver3);
        unregisterReceiver(mGattUpdateReceiver4);
        unregisterReceiver(mGattUpdateReceiver5);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService1 = null;
        mBluetoothLeService2 = null;
        mBluetoothLeService3 = null;
        mBluetoothLeService4 = null;
        mBluetoothLeService5 = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                if(!mConnected5)
                {
                    mBluetoothLeService5.connect(SensorFive);
                }
                else if(!mConnected4 && mConnected5)
                {
                    mBluetoothLeService4.connect(SensorFour);
                }
                else if(!mConnected3 && mConnected4 &&mConnected5)
                {
                    mBluetoothLeService3.connect(SensorThree);
                }
                else if(!mConnected2 && mConnected3 && mConnected4 && mConnected5)
                {
                    mBluetoothLeService2.connect(SensorTwo);
                }
                else if(!mConnected1&& mConnected2 && mConnected3 && mConnected4 && mConnected5 )
                {
                    mBluetoothLeService1.connect(SensorOne);
                }
                Toast.makeText(this, "1号："+mConnected1+" 2号："+mConnected2+" 3号："+mConnected3+" 4号："+mConnected4+" 5号："+mConnected5, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService1.disconnect();
                mBluetoothLeService2.disconnect();
                mBluetoothLeService3.disconnect();
                mBluetoothLeService4.disconnect();
                mBluetoothLeService5.disconnect();
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                return true;
            case android.R.id.home:
                onBackPressed();
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                return true;
            case R.id.about:
                Toast.makeText(this, "祝贺制作", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sensor1:
                Sensor_Number=0;
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "当前查看"+((int)(Sensor_Number+1))+"号传感器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sensor2:
                Sensor_Number=1;
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "当前查看"+((int)(Sensor_Number+1))+"号传感器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sensor3:
                Sensor_Number=2;
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "当前查看"+((int)(Sensor_Number+1))+"号传感器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sensor4:
                Sensor_Number=3;
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "当前查看"+((int)(Sensor_Number+1))+"号传感器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sensor5:
                Sensor_Number=4;
                btn_last.setVisibility(View.INVISIBLE);
                btn_next.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "当前查看"+((int)(Sensor_Number+1))+"号传感器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feature1:
                Sensor_Number=11;
                if(feature1_sta.size()>0) {
                    feature_draw(feature1_sta, feature1_Std);
                    if (feature_index[0][0] > 0) {
                        btn_last.setVisibility(View.VISIBLE);
                    } else {
                        btn_last.setVisibility(View.INVISIBLE);
                    }
                    if (feature_index[0][0] < feature_index[0][1]) {
                        btn_next.setVisibility(View.VISIBLE);
                    } else {
                        btn_next.setVisibility(View.INVISIBLE);
                    }
                    Toast.makeText(this, "当前查看特征1腰部加速度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征1腰部加速度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.feature2:
                Sensor_Number=12;
                if(feature2_sta.size()>0)
                {
                if(feature_index[1][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[1][0]<feature_index[1][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }

                    feature_draw(feature2_sta,feature2_Std);
                    Toast.makeText(this, "当前查看特征2左大腿角速度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征2左大腿角速度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                 break;
            case R.id.feature3:
                Sensor_Number=13;
                if(feature3_sta.size()>0)
                {
                if(feature_index[2][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[2][0]<feature_index[2][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }

                    feature_draw(feature3_sta,feature3_Std);
                    Toast.makeText(this, "当前查看特征3左大腿pitch角度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征3左大腿pitch角度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.feature4:
                Sensor_Number=14;
                if(feature4_sta.size()>0)
                {
                if(feature_index[3][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[3][0]<feature_index[3][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }

                    feature_draw(feature4_sta,feature4_Std);
                    Toast.makeText(this, "当前查看特征4右小腿角速度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征4右小腿角速度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.feature5:
                Sensor_Number=15;
                if(feature5_sta.size()>0)
                {
                if(feature_index[4][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[4][0]<feature_index[4][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }

                    feature_draw(feature5_sta,feature5_Std);
                    Toast.makeText(this, "当前查看特征5右小腿pitch角度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征5右小腿pitch角度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.feature6:
                Sensor_Number=16;
                if(feature6_sta.size()>0)
                {
                if(feature_index[5][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[5][0]<feature_index[5][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }

                    feature_draw(feature6_sta,feature6_Std);
                    Toast.makeText(this, "当前查看特征6左小腿角速度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征6左小腿角速度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                 break;
            case R.id.feature7:
                Sensor_Number=17;
                if(feature7_sta.size()>0)
                {
                if(feature_index[6][0]>0)
                {
                    btn_last.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_last.setVisibility(View.INVISIBLE);
                }
                if(feature_index[6][0]<feature_index[6][1])
                {
                    btn_next.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_next.setVisibility(View.INVISIBLE);
                }
                    feature_draw(feature7_sta,feature7_Std);
                    Toast.makeText(this, "当前查看特征7右大腿角速度实测和理想情况", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "请重新收集特征7右大腿角速度数据或点击查看上一次分析", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.lefthip:
//                Sensor_Number=5;
//                Toast.makeText(this, "当前查看左髋关节", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.righthip:
//                Sensor_Number=6;
//                Toast.makeText(this, "当前查看右髋关节", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.leftknee:
//                Sensor_Number=7;
//                Toast.makeText(this, "当前查看左膝关节", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.rightknee:
//                Sensor_Number=8;
//                Toast.makeText(this, "当前查看右膝关节", Toast.LENGTH_SHORT).show();
//                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private LineChartView linerChartInit() {//曲线控件
        LineChartView linerChart = (LineChartView) findViewById(R.id.linerChart);
        Viewport v = new Viewport(linerChart.getMaximumViewport());
        linerChart.setInteractive(false);
        v.bottom = -5;
        v.top = 15;
        linerChart.setCurrentViewport(v);
        linerChart.setMaximumViewport(v);
        linerChart.setZoomEnabled(false);
        return linerChart;
    }
    private void chartViewUpdate() {//更新表
        List<PointValue> xPointsValues = new ArrayList<>(100);
        List<PointValue> yPointsValues = new ArrayList<>(100);
        List<PointValue> zPointsValues = new ArrayList<>(100);
        int i = 0;
        for (dataVector index: chartDataQueue) {
            xPointsValues.add(new PointValue(i, (float)index.x));//添加对应的坐标值
            yPointsValues.add(new PointValue(i, (float)index.y));
            zPointsValues.add(new PointValue(i, (float)index.z));
            i++;
        }
        Line xline = new Line(xPointsValues).setColor(Color.argb(0xff,0x00,0x99,0xCC)).setCubic(true);//设置曲线颜色
        Line yline = new Line(yPointsValues).setColor(Color.argb(0xff,0x66,0x99,0x00)).setCubic(true);
        Line zline = new Line(zPointsValues).setColor(Color.argb(0xff,0xff,0x88,0x00)).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(xline);
        lines.add(yline);
        lines.add(zline);
        LineChartData data = new LineChartData();
        Axis xaxis = new Axis();
        Axis yaxis = new Axis();

        data.setAxisXBottom(xaxis);
        data.setAxisYLeft(yaxis);
        data.setLines(lines);

        linerChart.setLineChartData(data);
    }

    private void feature_draw(List<Double> feature_sta, double[] feature_std) {//在图上画出feature_sta和feature_std
        List<PointValue> xPointsValues = new ArrayList<>(feature_sta.size());
        List<PointValue> yPointsValues = new ArrayList<>(feature_std.length);
        int i = 0;
        for (i=0;i<feature_sta.size();i++) {
            xPointsValues.add(new PointValue(i, (float)(double)feature_sta.get(i)));//添加对应的坐标值
            i++;
        }
        for (i=0;i<feature_std.length;i++) {
            yPointsValues.add(new PointValue(i, (float)feature_std[i]));
            i++;
        }
        Line xline = new Line(xPointsValues).setColor(Color.argb(0xff,0x00,0x00,0xCC)).setCubic(true);//设置曲线颜色
        Line yline = new Line(yPointsValues).setColor(Color.argb(0xff,0xCC,0x00,0x00)).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(xline);
        lines.add(yline);
        LineChartData data = new LineChartData();
        Axis xaxis = new Axis();
        Axis yaxis = new Axis();

        data.setAxisXBottom(xaxis);
        data.setAxisYLeft(yaxis);
        data.setLines(lines);

        linerChart.setLineChartData(data);
    }
    public void UpdateDraw(double [] rec) {
        if (chartDataQueue.size() == 100) chartDataQueue.remove();
        chartDataQueue.add(new dataVector(rec[0],rec[1],rec[2]));
        pitchInfo.setText(String.format("Pitch:%.2f °",rec[0]));
        yawInfo.setText(String.format("Yaw:%.2f °",rec[1]));
        rollInfo.setText(String.format("Roll:%.2f °",rec[2]));
        chartViewUpdate();
    }

    private void displayData() {

//            Bt_pitch[0] = temp_t[0];//按顺序对应各个传感器信息
//            Bt_yaw[0] =   temp_t[1];
//            Bt_roll[0] = temp_t[2];
//            Bt_pitch[1] = temp_t[3];
//            Bt_yaw[1] =   temp_t[4];
//            Bt_roll[1] = temp_t[5];
//            test[0] = Bt_pitch[Sensor_Number];
//            test[1] = Bt_yaw[Sensor_Number];
//            test[2] = Bt_roll[Sensor_Number];
        if(mConnected1 && mConnected2 && mConnected3 && mConnected4 && mConnected5)
        {
            Hip_L = (float)-(Bt_pitch[1]-Bt_pitch[0])-Hip_L_temp;//通过传感器数值计算髋关节、膝关节角度
			Hip_R = (float)-(Bt_pitch[2]-Bt_pitch[0])-Hip_R_temp;
			Knee_L = (float)-(180-Bt_pitch[1]+Bt_pitch[3])-Knee_L_temp;
			Knee_R = (float)-(180-Bt_pitch[2]+Bt_pitch[4])-Knee_R_temp;
			Hip_L2 = (float) (Bt_roll[1]-Bt_roll[0])-Hip_L2_temp;
			Hip_R2 = (float) (Bt_roll[2]-Bt_roll[0])-Hip_R2_temp;
			Knee_L2 = (float) (180-Bt_roll[1]+Bt_roll[3])-Knee_L2_temp;
			Knee_R2 = (float) (180-Bt_roll[2]+Bt_roll[4])-Knee_R2_temp;
			test[5][0]=Hip_L;
            test[5][1]=Hip_L2;
            test[5][2]=0;
            test[6][0]=Hip_R;
            test[6][1]=Hip_R;
            test[6][2]=0;
            test[7][0]=Knee_L;
            test[7][1]=Knee_L2;
            test[7][2]=0;
            test[8][0]=Knee_R;
            test[8][1]=Knee_R2;
            test[8][2]=0;

            FrameLayout Human_model = (FrameLayout) findViewById(R.id.frameLayout1);//更新人物模型动作
            Human_model.removeAllViews();
            Human_model.addView(myView);

//            Human_model.addView(new MyView(DeviceControlActivity.this));
        }
        if(Sensor_Number<5)
        {
            UpdateDraw(test[Sensor_Number]);//刷新曲线图
        }
        }





    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.SensorOne+BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorOne+BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorOne+BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.SensorOne+BluetoothLeService.ACTION_DATA_AVAILABLE1);
        intentFilter.addAction(BluetoothLeService.SensorTwo+BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorTwo+BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorTwo+BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.SensorTwo+BluetoothLeService.ACTION_DATA_AVAILABLE2);
        intentFilter.addAction(BluetoothLeService.SensorThree+BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorThree+BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorThree+BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.SensorThree+BluetoothLeService.ACTION_DATA_AVAILABLE3);
        intentFilter.addAction(BluetoothLeService.SensorFour+BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorFour+BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorFour+BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.SensorFour+BluetoothLeService.ACTION_DATA_AVAILABLE4);
        intentFilter.addAction(BluetoothLeService.SensorFive+BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorFive+BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.SensorFive+BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.SensorFive+BluetoothLeService.ACTION_DATA_AVAILABLE5);
        return intentFilter;
    }

    public void onConnectButtonClicked(View v)
    {
        Hip_L_temp=Hip_L;
        Hip_R_temp=Hip_R;
        Knee_L_temp=Knee_L;
        Knee_R_temp=Knee_R;
        Hip_L2_temp=Hip_L2;
        Hip_R2_temp=Hip_R2;
        Knee_L2_temp=Knee_L2;
        Knee_R2_temp=Knee_R2;
        if(Isfeedback)
        {
            mBluetoothLeService5.writeByes(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0e,(byte)0x03,(byte)0x00});//端口0 低电平输出
            Isfeedback=false;
        }
        else
        {
            Isfeedback=true;
        }
    }

    private class dataVector { //用来存三个数据的集合
        double x, y, z;

        dataVector(double ix, double iy, double iz) {
            x = ix;
            y = iy;
            z = iz;
        }
        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd-HH:mm:ss");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        public String toString() {
            return String.format(str+",%.2f,%.2f,%.2f\r", x, y, z);
        }
    }
    private class SensorVector { //用来存三个数据的集合
        double Sensor_Acc_x,Sensor_Acc_y,Sensor_Acc_z,Sensor_Agl_x,Sensor_Agl_y,Sensor_Agl_z,Sensor_pitch,Sensor_yaw,Sensor_roll;

        SensorVector(double iSensor_Acc_x, double iSensor_Acc_y, double iSensor_Acc_z,double iSensor_Agl_x,double iSensor_Agl_y,double iSensor_Agl_z,double iSensor_pitch,double iSensor_yaw,double iSensor_roll) {
            Sensor_Acc_x = iSensor_Acc_x;
            Sensor_Acc_y = iSensor_Acc_y;
            Sensor_Acc_z = iSensor_Acc_z;
            Sensor_Agl_x = iSensor_Agl_x;
            Sensor_Agl_y = iSensor_Agl_y;
            Sensor_Agl_z = iSensor_Agl_z;
            Sensor_pitch = iSensor_pitch;
            Sensor_yaw = iSensor_yaw;
            Sensor_roll = iSensor_roll;
        }
        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd-HH:mm:ss");
        Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        public String toString() {
            return String.format(str+",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,",Sensor_Acc_x, Sensor_Acc_y, Sensor_Acc_z,Sensor_Agl_x,Sensor_Agl_y,Sensor_Agl_z,Sensor_pitch,Sensor_yaw,Sensor_roll);
        }
    }

    public void onOpenButtonClicked(View v) {
        if (!isonRecoding) {
            feature1.clear();
            feature2.clear();
            feature3.clear();
            feature4.clear();
            feature5.clear();
            feature6.clear();
            feature7.clear();
            feature1_sta.clear();
            feature2_sta.clear();
            feature3_sta.clear();
            feature4_sta.clear();
            feature5_sta.clear();
            feature6_sta.clear();
            feature7_sta.clear();
            for(int i=0;i<7;i++) {
                for(int j=0;j<2;j++)
                {
                    feature_index[i][j]=0;
                }
            }
            feature_notice.setText("");
            btn_record.setText("停止记录");
            btn_analy.setText("请先采集数据");
            isonRecoding = true;
        } else {
            btn_record.setText("开始记录");
            try {
                SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd-HH:mm:ss");
                Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                File f = new File(getExternalStorageDirectory().toString()+"/" +str+ FILENAME);
                if (!f.exists())
                    f.createNewFile();
                FileOutputStream output = new FileOutputStream(f, false);
                String x = "";
                while (!dataCache1.isEmpty()&&!dataCache2.isEmpty()&&!dataCache3.isEmpty()&&!dataCache4.isEmpty()&&!dataCache5.isEmpty()) {
                    temp = dataCache1.poll().toString()+dataCache2.poll().toString()+dataCache3.poll().toString()+dataCache4.poll().toString()+dataCache5.poll().toString()+"\r";
                    output.write(temp.getBytes());
                }
                output.close();
                Toast.makeText(getApplicationContext(),("文件已保存到" + getExternalStorageDirectory().toString()+ "/"+str + FILENAME),Toast.LENGTH_LONG).show();
                btn_analy.setText("开始分析");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"文件保存出错",Toast.LENGTH_LONG).show();
            }
            isonRecoding = false;
        }
    }
    public class onAnalyClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //onPause();
            for(int i=0;i<7;i++) {
                for(int j=0;j<2;j++)
                {
                    feature_index[i][j]=0;
                }
            }
            feature_notice.setText("");
            feature1_sta.clear();
            feature2_sta.clear();
            feature3_sta.clear();
            feature4_sta.clear();
            feature5_sta.clear();
            feature6_sta.clear();
            feature7_sta.clear();
            feature_analy(feature1,feature1_Std,1,0.2,20);
            feature_analy(feature2,feature2_Std,2,0.2,20);
            feature_analy(feature3,feature3_Std,3,0.2,20);
            feature_analy(feature4,feature4_Std,4,0.2,20);//step慢走在56左右
            feature_analy(feature5,feature5_Std,5,0.2,20);//函数返回平均差异值，通过右腿pitch角来划分步态周期
            feature_analy(feature6,feature6_Std,6,0.2,20);
            feature_analy(feature7,feature7_Std,7,0.2,20);
//            feature_analy2(feature5,feature5_Std,5);//函数返回平均差异值，通过右腿pitch角来划分步态周期
//            feature_analy2(feature4,feature4_Std,4);
//            feature_analy2(feature3,feature3_Std,3);
//            feature_analy2(feature2,feature2_Std,2);
//            feature_analy2(feature1,feature1_Std,1);
           // onResume();
        }
    }

    public double feature_analy(List<Double> feature,double[] feature_Std,int fea_num,double precent,int step)//步态信号划分、分析、dtw计算距离
    {
        double Data_max=feature.get(0);
        double Data_min=feature.get(0);
        double Divergence=0;
        int i_max=0;
        int i_min=0;
        double A_temp;
        for(int i=0;i<feature.size();i++)
        {
            A_temp=feature.get(i);
            if(A_temp>Data_max)
            {
                Data_max=A_temp;
                i_max=i;
            }
            if(A_temp<Data_min)
            {
                Data_min=A_temp;
                i_min=i;
            }
        }
        double Threshold_down=Data_min+precent*(Data_max-Data_min);
        List i_slice_down = new ArrayList();
        for(int i=1;i<feature.size()-1;i++)
        {
            if(feature.get(i)<Threshold_down)
            {
                if ((feature.get(i + 1) > feature.get(i)) && (feature.get(i - 1) > feature.get(i)))
                {
                    i_slice_down.add(i);
                }
            }
        }
        List Slice_temp = new ArrayList();
        for(int m=0;m<i_slice_down.size()-1;m++)
        {
            A_temp=(int)(i_slice_down.get(m+1))-(int)(i_slice_down.get(m));
            if(A_temp>step)//如果相邻两片小于步长则抹除其中一个,大于步长的予以保留
            {
                Slice_temp.add(((int)i_slice_down.get(m)));
                Log.e(TAG,""+i_slice_down.get(m));
            }
        }
        if(Slice_temp.size()>2)
        {
        List feature_test = new ArrayList();
        feature_test.addAll(feature);
        double max_temp=(double)feature_test.get((int)Slice_temp.get(0));
        double min_temp=(double)feature_test.get((int)Slice_temp.get(0));
            for (int k = 0; k < Slice_temp.size() - 1; k++) {
                for (int i = ((int) Slice_temp.get(k)); i < ((int) Slice_temp.get(k + 1)); i++) {
                    if ((double) feature_test.get(i) > max_temp) {
                        max_temp = (double) feature_test.get(i);
                    }
                    if ((double) feature_test.get(i) < min_temp) {
                        min_temp = (double) feature_test.get(i);
                    }
                }

                for (int j = (int) Slice_temp.get(k); j < (int) Slice_temp.get(k + 1); j++) {
                    feature_test.set(j, ((double) feature_test.get(j) - min_temp) / (max_temp - min_temp));//按一个步态周期进行数据标准化
                }
                Dtw dtw = new Dtw();
                double dtw_temp = dtw.getDistance(feature_test.subList((int) Slice_temp.get(k), (int) Slice_temp.get(k + 1)), feature_Std);
                Divergence = Divergence + dtw_temp;
                switch (fea_num)//单独划分周期时用 测试查看用
                {
                    case 1:
                        Log.e(TAG, "特征1腰部共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 2:
                        Log.e(TAG, "特征2左大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 3:
                        Log.e(TAG, "特征3左大腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 4:
                        Log.e(TAG, "特征4右小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 5:
                        Log.e(TAG, "特征5右小腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 6:
                        Log.e(TAG, "特征6左小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                    case 7:
                        Log.e(TAG, "特征7右大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (k + 1) + "个周期差异性为：" + dtw_temp);
                        break;
                }
            }
            switch (fea_num)//单独划分周期时用
            {
                case 1:
                    feature_index[0][1] = (Slice_temp.size() - 1);
                    feature1_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[0][0]), (int) Slice_temp.get(feature_index[0][0] + 1)));
                    break;
                case 2:
                    feature_index[1][1] = (Slice_temp.size() - 1);
                    feature2_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[1][0]), (int) Slice_temp.get(feature_index[1][0] + 1)));
                    break;
                case 3:
                    feature_index[2][1] = (Slice_temp.size() - 1);
                    feature3_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[2][0]), (int) Slice_temp.get(feature_index[2][0] + 1)));
                    break;
                case 4:
                    feature_index[3][1] = (Slice_temp.size() - 1);
                    feature4_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[3][0]), (int) Slice_temp.get(feature_index[3][0] + 1)));
                    break;
                case 5:
                    feature_index[4][1] = (Slice_temp.size() - 1);
                    feature5_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[4][0]), (int) Slice_temp.get(feature_index[4][0] + 1)));
                    break;
                case 6:
                    feature_index[5][1] = (Slice_temp.size() - 1);
                    feature6_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[5][0]), (int) Slice_temp.get(feature_index[5][0] + 1)));
                    break;
                case 7:
                    feature_index[6][1] = (Slice_temp.size() - 1);
                    feature7_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[6][0]), (int) Slice_temp.get(feature_index[6][0] + 1)));
                    break;
            }
            btn_analy.setText("查看上一次分析");
            return Divergence / (Slice_temp.size() - 1);

        }
        else
        {
           switch(fea_num)//单独划分周期时用
           {
               case 1:Toast.makeText(getApplicationContext(),"特征1腰部步态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 2:Toast.makeText(getApplicationContext(),"特征2左大腿角度步态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 3:Toast.makeText(getApplicationContext(),"特征3左大腿角速度态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 4:Toast.makeText(getApplicationContext(),"特征4右小腿角度步态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 5:Toast.makeText(getApplicationContext(),"特征5右小腿角速度步态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 6:Toast.makeText(getApplicationContext(),"特征6左小腿角度步态周期划分失败",Toast.LENGTH_LONG).show();break;
               case 7:Toast.makeText(getApplicationContext(),"特征7右大腿角度步态周期划分失败",Toast.LENGTH_LONG).show();break;
           }
            return 0;
        }
    }

    public void feature_slice_draw(List<Double> feature,double[] feature_Std,int fea_num,double precent,int step)//相当于重写了feature_analy函数,主要是将对应的步态片段写入feature_sta中，方便后面显示用
    {
        double Data_max=feature.get(0);
        double Data_min=feature.get(0);
        double Divergence=0;
        int i_max=0;
        int i_min=0;
        double A_temp;
        for(int i=0;i<feature.size();i++)
        {
            A_temp=feature.get(i);
            if(A_temp>Data_max)
            {
                Data_max=A_temp;
                i_max=i;
            }
            if(A_temp<Data_min)
            {
                Data_min=A_temp;
                i_min=i;
            }
        }
        double Threshold_down=Data_min+precent*(Data_max-Data_min);
        List i_slice_down = new ArrayList();
        for(int i=1;i<feature.size()-1;i++)
        {
            if(feature.get(i)<Threshold_down)
            {
                if ((feature.get(i + 1) > feature.get(i)) && (feature.get(i - 1) > feature.get(i)))
                {
                    i_slice_down.add(i);
                }
            }
        }
        List Slice_temp = new ArrayList();
        for(int m=0;m<i_slice_down.size()-1;m++)
        {
            A_temp=(int)(i_slice_down.get(m+1))-(int)(i_slice_down.get(m));
            if(A_temp>step)//如果相邻两片小于步长则抹除其中一个,大于步长的予以保留
            {
                Slice_temp.add(((int)i_slice_down.get(m)));
                Log.e(TAG,""+i_slice_down.get(m));
            }
        }
        if(Slice_temp.size()>2) {
            List feature_test = new ArrayList();
            feature_test.addAll(feature);
            double max_temp = (double) feature_test.get((int) Slice_temp.get(0));
            double min_temp = (double) feature_test.get((int) Slice_temp.get(0));
            for (int i = ((int) Slice_temp.get(feature_index[Sensor_Number - 11][0])); i < ((int) Slice_temp.get(feature_index[Sensor_Number - 11][0] + 1)); i++) {
                if ((double) feature_test.get(i) > max_temp) {
                    max_temp = (double) feature_test.get(i);
                }
                if ((double) feature_test.get(i) < min_temp) {
                    min_temp = (double) feature_test.get(i);
                }
            }

            for (int j = (int) Slice_temp.get(feature_index[Sensor_Number - 11][0]); j < (int) Slice_temp.get(feature_index[Sensor_Number - 11][0] + 1); j++) {
                feature_test.set(j, ((double) feature_test.get(j) - min_temp) / (max_temp - min_temp));//按一个步态周期进行数据标准化
            }
            Dtw dtw = new Dtw();
            double dtw_temp = dtw.getDistance(feature_test.subList((int) Slice_temp.get(feature_index[Sensor_Number - 11][0]), (int) Slice_temp.get(feature_index[Sensor_Number - 11][0] + 1)), feature_Std);
            switch (fea_num)//单独划分周期时用
            {
                case 1:
                    Log.e(TAG, "特征1腰部共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[0][1] = (Slice_temp.size() - 1);
                    feature1_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[0][0]), (int) Slice_temp.get(feature_index[0][0] + 1)));
                    feature_notice.setText("特征1腰部共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征1腰部共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Log.e(TAG, "特征2左大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[1][1] = (Slice_temp.size() - 1);
                    feature2_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[1][0]), (int) Slice_temp.get(feature_index[1][0] + 1)));
                    feature_notice.setText("特征2左大腿角度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征2左大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Log.e(TAG, "特征3左大腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[2][1] = (Slice_temp.size() - 1);
                    feature3_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[2][0]), (int) Slice_temp.get(feature_index[2][0] + 1)));
                    feature_notice.setText("特征3左大腿角速度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征3左大腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Log.e(TAG, "特征4右小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[3][1] = (Slice_temp.size() - 1);
                    feature4_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[3][0]), (int) Slice_temp.get(feature_index[3][0] + 1)));
                    feature_notice.setText("特征4右小腿角度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征4右小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Log.e(TAG, "特征5右小腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[4][1] = (Slice_temp.size() - 1);
                    feature5_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[4][0]), (int) Slice_temp.get(feature_index[4][0] + 1)));
                    feature_notice.setText("特征5右小腿角速度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征5右小腿角速度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Log.e(TAG, "特征6左小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[5][1] = (Slice_temp.size() - 1);
                    feature6_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[5][0]), (int) Slice_temp.get(feature_index[5][0] + 1)));
                    feature_notice.setText("特征6左小腿角度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征6左小腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Log.e(TAG, "特征7右大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp);
                    feature_index[6][1] = (Slice_temp.size() - 1);
                    feature7_sta.addAll(feature_test.subList((int) Slice_temp.get(feature_index[6][0]), (int) Slice_temp.get(feature_index[6][0] + 1)));
                    feature_notice.setText("特征7右大腿角度共" + (Slice_temp.size() - 1) + "个周期\n第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + (double)Math.round(dtw_temp*100)/100);
                    Toast.makeText(getApplicationContext(), "特征7右大腿角度共" + (Slice_temp.size() - 1) + "个周期,第" + (feature_index[Sensor_Number - 11][0] + 1) + "个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
                    break;
            }
        }

    }
//    public double feature_analy2(List<Double> feature,double[] feature_Std,int fea_num)//只用右小腿pitch信号进行步态周期划分
//    {
//        double Data_max=feature.get(0);
//        double Data_min=feature.get(0);
//        double Divergence=0;
//        int i_max=0;
//        int i_min=0;
//        double A_temp;
//        for(int i=0;i<feature.size();i++)
//        {
//            A_temp=feature.get(i);
//            if(A_temp>Data_max)
//            {
//                Data_max=A_temp;
//                i_max=i;
//            }
//            if(A_temp<Data_min)
//            {
//                Data_min=A_temp;
//                i_min=i;
//            }
//        }
//        if(fea_num==5) {
//            double Threshold_down = Data_min + 0.2 * (Data_max - Data_min);
//            int step = 10;
//            List i_slice_down = new ArrayList();
//            for (int i = 1; i < feature.size() - 1; i++) {
//                if (feature.get(i) < Threshold_down) {
//                    if ((feature.get(i + 1) > feature.get(i)) && (feature.get(i - 1) > feature.get(i))) {
//                        i_slice_down.add(i);
//                    }
//                }
//            }
//            //List Slice_temp = new ArrayList();
//            for (int m = 0; m < i_slice_down.size() - 1; m++) {
//                A_temp = (int) (i_slice_down.get(m + 1)) - (int) (i_slice_down.get(m));
//                if (A_temp > step)//如果相邻两片小于步长则抹除其中一个,大于步长的予以保留
//                {
//                    Slice_static.add(((int) i_slice_down.get(m)));
//                    Log.e(TAG, "" + i_slice_down.get(m));
//                }
//            }
//        }
//        List feature_test = new ArrayList();
//        feature_test.addAll(feature);
//        double max_temp=(double)feature_test.get((int)Slice_static.get(0));
//        double min_temp=(double)feature_test.get((int)Slice_static.get(0));
//        if(Slice_static.size()>2)
//        {
//            for (int k = 0; k < Slice_static.size() - 1; k++) {
//                for (int i = ((int) Slice_static.get(k)); i < ((int) Slice_static.get(k + 1)); i++) {
//                    if ((double)feature_test.get(i) > max_temp) {
//                        max_temp = (double)feature_test.get(i);
//                    }
//                    if ((double)feature_test.get(i) < min_temp) {
//                        min_temp = (double)feature_test.get(i);
//                    }
//                }
//
//                for (int j = (int) Slice_static.get(k); j < (int) Slice_static.get(k + 1); j++) {
//                    feature_test.set(j, ((double)feature_test.get(j) - min_temp) / (max_temp - min_temp));//按一个步态周期进行数据标准化
//                }
//                Dtw dtw = new Dtw();
//                double dtw_temp=dtw.getDistance(feature_test.subList((int) Slice_static.get(k), (int) Slice_static.get(k+1)), feature_Std);
//                Divergence=Divergence+dtw_temp;
//                Log.e(TAG,"共" + (Slice_static.size()-1)  + "个周期,第"+(k+1)+"个周期差异性为："  + dtw_temp);
//                Toast.makeText(getApplicationContext(), "共" + (Slice_static.size()-1) + "个周期,第"+(k+1)+"个周期差异性为：" + dtw_temp, Toast.LENGTH_LONG).show();
//                btn_analy.setText("请先采集数据");
//            }
//            return Divergence/(Slice_static.size()-1);
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(),"步态周期划分失败",Toast.LENGTH_LONG).show();
//            return 0;
//        }
//    }



    public class Dtw {

        public double getMin(double a, double b, double c) {
            double min = a;
            if (b > a)
                min = a;
            else if (c > b) {
                min = b;
            } else {
                min = c;
            }
            return min;
        }

        public double getDistance(List<Double> seqa, double[] seqb) {
            double distance = 0;
            int lena = seqa.size();
            int lenb = seqb.length;
            double[][] c = new double[lena][lenb];
            for (int i = 0; i < lena; i++) {
                for (int j = 0; j < lenb; j++) {
                    c[i][j] = 1;
                }
            }
            for (int i = 0; i < lena; i++) {
                for (int j = 0; j < lenb; j++) {
                    double tmp = Math.sqrt(((double)seqa.get(i) - seqb[j]) * ((double)seqa.get(i) - seqb[j]));
                    if (j == 0 && i == 0)
                        c[i][j] = tmp;
                    else if (j > 0)
                        c[i][j] = c[i][j - 1] + tmp;
                    if (i > 0) {
                        if (j == 0)
                            c[i][j] = tmp + c[i - 1][j];
                        else
                            c[i][j] = tmp + getMin(c[i][j - 1], c[i - 1][j - 1], c[i - 1][j]);
                    }
                }
            }
            distance = c[lena - 1][lenb - 1];
            return distance;
        }

    }

    public class onlastClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            feature_index[Sensor_Number-11][0]--;
            if(feature_index[Sensor_Number-11][0]<=0)
            {
                feature_index[Sensor_Number-11][0]=0;
                btn_last.setVisibility(View.INVISIBLE);
            }
            if(feature_index[Sensor_Number-11][0]<feature_index[Sensor_Number-11][1])
            {
                btn_next.setVisibility(View.VISIBLE);
            }
            feature_notice.setText("");
            switch(Sensor_Number){
                case 11:feature1_sta.clear();
                        feature_slice_draw(feature1,feature1_Std,1,0.2,20);
                        feature_draw(feature1_sta,feature1_Std);break;
                case 12:feature2_sta.clear();
                        feature_slice_draw(feature2,feature2_Std,2,0.2,20);
                        feature_draw(feature2_sta,feature2_Std);break;
                case 13:feature3_sta.clear();
                        feature_slice_draw(feature3,feature3_Std,3,0.2,20);
                        feature_draw(feature3_sta,feature3_Std);break;
                case 14:feature4_sta.clear();
                        feature_slice_draw(feature4,feature4_Std,4,0.2,20);
                        feature_draw(feature4_sta,feature4_Std);break;
                case 15:feature5_sta.clear();
                        feature_slice_draw(feature5,feature5_Std,5,0.2,20);
                        feature_draw(feature5_sta,feature5_Std);break;
                case 16:feature6_sta.clear();
                        feature_slice_draw(feature6,feature6_Std,6,0.2,20);
                        feature_draw(feature6_sta,feature6_Std);break;
                case 17:feature7_sta.clear();
                        feature_slice_draw(feature7,feature7_Std,7,0.2,20);
                        feature_draw(feature7_sta,feature7_Std);break;
            }

        }
    }

    public class onnextClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            feature_index[Sensor_Number-11][0]++;
            if(feature_index[Sensor_Number-11][0]>=feature_index[Sensor_Number-11][1]-1)
            {
                btn_next.setVisibility(View.INVISIBLE);
                feature_index[Sensor_Number-11][0]=feature_index[Sensor_Number-11][1]-1;
            }
            if(feature_index[Sensor_Number-11][0]>0)
            {
                btn_last.setVisibility(View.VISIBLE);
            }
            feature_notice.setText("");
            switch(Sensor_Number){
                case 11:feature1_sta.clear();
                    feature_slice_draw(feature1,feature1_Std,1,0.2,20);
                    feature_draw(feature1_sta,feature1_Std);break;
                case 12:feature2_sta.clear();
                    feature_slice_draw(feature2,feature2_Std,2,0.2,20);
                    feature_draw(feature2_sta,feature2_Std);break;
                case 13:feature3_sta.clear();
                    feature_slice_draw(feature3,feature3_Std,3,0.2,20);
                    feature_draw(feature3_sta,feature3_Std);break;
                case 14:feature4_sta.clear();
                    feature_slice_draw(feature4,feature4_Std,4,0.2,20);
                    feature_draw(feature4_sta,feature4_Std);break;
                case 15:feature5_sta.clear();
                    feature_slice_draw(feature5,feature5_Std,5,0.2,20);
                    feature_draw(feature5_sta,feature5_Std);break;
                case 16:feature6_sta.clear();
                    feature_slice_draw(feature6,feature6_Std,6,0.2,20);
                    feature_draw(feature6_sta,feature6_Std);break;
                case 17:feature7_sta.clear();
                    feature_slice_draw(feature7,feature7_Std,7,0.2,20);
                    feature_draw(feature7_sta,feature7_Std);break;
            }

        }
    }

    public class onfeedbackClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(Isfeedback)
            {
                Isfeedback=false;
                btn_feedback.setText("开启反馈");
            }
            else{
                Isfeedback=true;
                btn_feedback.setText("关闭反馈");
            }
        }
    }
}
