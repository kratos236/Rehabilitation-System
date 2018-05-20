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

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BluetoothLeService extends Service {
    //private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final static String TAG="Data";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private int mConnectionState = STATE_DISCONNECTED;
    private boolean SConnected1 = false;
    private boolean SConnected2 = false;
    private boolean SConnected3 = false;
    private boolean SConnected4 = false;
    private boolean SConnected5 = false;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE1 =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE1";
    public final static String ACTION_DATA_AVAILABLE2 =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE2";
    public final static String ACTION_DATA_AVAILABLE3 =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE3";
    public final static String ACTION_DATA_AVAILABLE4 =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE4";
    public final static String ACTION_DATA_AVAILABLE5 =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE5";
    public final static String EXTRA_DATA1 =
            "com.example.bluetooth.le.EXTRA_DATA1";
    public final static String EXTRA_DATA2 =
            "com.example.bluetooth.le.EXTRA_DATA2";
    public final static String EXTRA_DATA3 =
            "com.example.bluetooth.le.EXTRA_DATA3";
    public final static String EXTRA_DATA4 =
            "com.example.bluetooth.le.EXTRA_DATA4";
    public final static String EXTRA_DATA5 =
            "com.example.bluetooth.le.EXTRA_DATA5";
//    public final static String SensorOne  = "EE:EC:6B:A6:EB:22";
    public final static String SensorOne  = "D1:FB:17:39:B5:5F";
    public final static String SensorTwo  = "C3:99:F4:D8:19:18";
    public final static String SensorThree  = "C1:23:41:C3:43:E0";
    public final static String SensorFour  = "E8:1C:46:BD:44:C1";
    //public final static String SensorFive  = "C3:A2:70:70:93:34";
//    public final static String SensorFive  = "D4:8E:96:16:9C:2E";
    public final static String SensorFive  = "EC:58:2E:8D:30:A2";
    //String Sensor1_data,Sensor2_data,Sensor3_data,Sensor4_data,Sensor5_data;

    public final static UUID UUID_HEART_RATE_MEASUREMENT =UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            BluetoothDevice dev=gatt.getDevice();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = dev.getAddress()+ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                switch(dev.getAddress()) {
                    case SensorOne:SConnected1=true;break;
                    case SensorTwo:SConnected2=true;break;
                    case SensorThree:SConnected3=true;break;
                    case SensorFour:SConnected4=true;break;
                    case SensorFive:SConnected5=true;break;
                }
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = dev.getAddress()+ACTION_GATT_DISCONNECTED;
                switch(dev.getAddress()) {
                    case SensorOne:SConnected1=false;break;
                    case SensorTwo:SConnected2=false;break;
                    case SensorThree:SConnected3=false;break;
                    case SensorFour:SConnected4=false;break;
                    case SensorFive:SConnected5=false;break;
                }

                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothDevice dev=gatt.getDevice();
                broadcastUpdate(dev.getAddress()+ACTION_GATT_SERVICES_DISCOVERED);
                BluetoothLeService.this.getWorkableGattServices(getSupportedGattServices());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            BluetoothDevice dev=gatt.getDevice();
            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.i(TAG, "--onCharacteristicRead called--");
                byte[] sucString=characteristic.getValue();
                //String string= new String(sucString);
                switch(dev.getAddress()) {
                    case SensorOne:
                        BluetoothGattCharacteristic characteristic1=characteristic;
                        mBluetoothDeviceAddress=dev.getAddress();
                        broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE1, characteristic1);
                        break;
                    case SensorTwo:
                        BluetoothGattCharacteristic characteristic2=characteristic;
                        mBluetoothDeviceAddress=dev.getAddress();
                        broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE2, characteristic2);
                        break;
                    case SensorThree:
                        BluetoothGattCharacteristic characteristic3=characteristic;
                        mBluetoothDeviceAddress=dev.getAddress();
                        broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE3, characteristic3);
                        break;
                    case SensorFour:
                        BluetoothGattCharacteristic characteristic4=characteristic;
                        mBluetoothDeviceAddress=dev.getAddress();
                        broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE4, characteristic4);
                        break;
                    case SensorFive:
                        BluetoothGattCharacteristic characteristic5=characteristic;
                        mBluetoothDeviceAddress=dev.getAddress();
                        broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE5, characteristic5);
                        break;
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            BluetoothDevice dev=gatt.getDevice();
           // Log.e(TAG,"Dev: "+dev.getAddress());
            switch(dev.getAddress()) {
                case SensorOne:
                    BluetoothGattCharacteristic characteristic1=characteristic;
                    mBluetoothDeviceAddress=dev.getAddress();
                    broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE1, characteristic1);
                    break;
                case SensorTwo:
                    BluetoothGattCharacteristic characteristic2=characteristic;
                    mBluetoothDeviceAddress=dev.getAddress();
                    broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE2, characteristic2);
                    break;
                case SensorThree:
                    BluetoothGattCharacteristic characteristic3=characteristic;
                    mBluetoothDeviceAddress=dev.getAddress();
                    broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE3, characteristic3);
                    break;
                case SensorFour:
                    BluetoothGattCharacteristic characteristic4=characteristic;
                    mBluetoothDeviceAddress=dev.getAddress();
                    broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE4, characteristic4);
                    break;
                case SensorFive:
                    BluetoothGattCharacteristic characteristic5=characteristic;
                    mBluetoothDeviceAddress=dev.getAddress();
                    broadcastUpdate(dev.getAddress() + ACTION_DATA_AVAILABLE5, characteristic5);
                    break;
            }
        }
        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){

        }
    };
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private Queue<Byte> queueBuffer = new LinkedList<Byte>();
    private byte[] packBuffer1 = new byte[20];
    private byte[] packBuffer2 = new byte[20];
    private byte[] packBuffer3 = new byte[20];
    float d=0;

    String strIMU1="",strIMU2="",strIMU3="",strIMU4="",strIMU5="",strACK="",strIO="";

    public float fAngleRef;
    float Yaw;
    public void SetAngleRef(boolean bRef)
    {
        if (bRef) fAngleRef = Yaw;
        else fAngleRef = 0;
    };
    public boolean getRssiVal() {
        if (mBluetoothGatt == null)
            return false;
        return mBluetoothGatt.readRemoteRssi();

    }
    byte byteIO=0;
    float fPressure=0;
    private void broadcastUpdate(final String action,final BluetoothGattCharacteristic characteristic) {
        switch(mBluetoothDeviceAddress) {
            case SensorOne:
                final Intent intent1 = new Intent(action);
                final byte[] packBuffer1 = characteristic.getValue();
                float[] fData1 = new float[9];
                if (packBuffer1 != null && packBuffer1.length == 20) {
                    switch (packBuffer1[1]) {
                        case 0x61:
                            for (int i = 0; i < 9; i++)
                                fData1[i] = (((short) packBuffer1[i * 2 + 3]) << 8) | ((short) packBuffer1[i * 2 + 2] & 0xff);
                            for (int i = 0; i < 3; i++)
                                fData1[i] = (float) (fData1[i] / 32768.0 * 16.0);
                            for (int i = 3; i < 6; i++)
                                fData1[i] = (float) (fData1[i] / 32768.0 * 2000.0);
                            for (int i = 6; i < 9; i++)
                                fData1[i] = (float) (fData1[i] / 32768.0 * 180.0);
                            //strIMU1 = (String.format("%.2f %.2f %.2f ", fData1[6], fData1[7], fData1[8]));
                            //strIMU1 = (String.format("%.2f %.2f %.2f ", 0.1, 0.1, 0.1));
                            strIMU1 = (String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f", fData1[0], fData1[1], fData1[2],fData1[3], fData1[4], fData1[5],fData1[6], fData1[7], fData1[8]));
                            intent1.putExtra(EXTRA_DATA1, mBluetoothDeviceAddress + "," + strIMU1);
                            sendBroadcast(intent1);
//                            SimpleDateFormat formatter1    =   new    SimpleDateFormat    ("yyyy年MM月dd日HH:mm:ss");
//                            Date curDate1    =   new    Date(System.currentTimeMillis());//获取当前时间
//                            String  str1 =  formatter1.format(curDate1);
//                            Sensor1_data=(str1+String.format(",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f", fData1[0], fData1[1], fData1[2],fData1[3], fData1[4], fData1[5],fData1[6], fData1[7], fData1[8]));
//                            Log.e(TAG, "Sensor1:"  + Sensor1_data);
                            //mBluetoothDeviceAddress=SensorTwo;
//                            if(SConnected1 && SConnected2 && SConnected3 && SConnected4 && SConnected5)
//                            {
//                                dataCache1.add(new dataVector(fData1[0], fData1[1], fData1[2],fData1[3], fData1[4], fData1[5],fData1[6], fData1[7], fData1[8]));
//                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case SensorTwo:
                final Intent intent2 = new Intent(action);
                final byte[] packBuffer2 = characteristic.getValue();
                float[] fData2 = new float[9];
                if (packBuffer2 != null && packBuffer2.length == 20) {
                    switch (packBuffer2[1]) {
                        case 0x61:
                            for (int i = 0; i < 9; i++)
                                fData2[i] = (((short) packBuffer2[i * 2 + 3]) << 8) | ((short) packBuffer2[i * 2 + 2] & 0xff);
                            for (int i = 0; i < 3; i++)
                                fData2[i] = (float) (fData2[i] / 32768.0 * 16.0);
                            for (int i = 3; i < 6; i++)
                                fData2[i] = (float) (fData2[i] / 32768.0 * 2000.0);
                            for (int i = 6; i < 9; i++)
                                fData2[i] = (float) (fData2[i] / 32768.0 * 180.0);
                            //strIMU2 = (String.format("%.2f %.2f %.2f ", fData2[6], fData2[7], fData2[8]));
                            //strIMU2 = (String.format("%.2f %.2f %.2f ", 0.2, 0.2, 0.2));
                            strIMU2 = (String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",fData2[0], fData2[1], fData2[2],fData2[3], fData2[4], fData2[5],fData2[6], fData2[7], fData2[8]));
                            intent2.putExtra(EXTRA_DATA2, mBluetoothDeviceAddress + "," + strIMU2);
                            sendBroadcast(intent2);
//                            if(SConnected1 && SConnected2 && SConnected3 && SConnected4 && SConnected5)
//                            {
//                                dataCache2.add(new dataVector(fData2[0], fData2[1], fData2[2],fData2[3], fData2[4], fData2[5],fData2[6], fData2[7], fData2[8]));
//                            }
                            //mBluetoothDeviceAddress=SensorThree;
                            break;
                        default:
                            break;
                    }
                }
                break;
            case SensorThree:
                final Intent intent3 = new Intent(action);
                final byte[] packBuffer3 = characteristic.getValue();
                float[] fData3 = new float[9];
                if (packBuffer3 != null && packBuffer3.length == 20) {
                    switch (packBuffer3[1]) {
                        case 0x61:
                            for (int i = 0; i < 9; i++)
                                fData3[i] = (((short) packBuffer3[i * 2 + 3]) << 8) | ((short) packBuffer3[i * 2 + 2] & 0xff);
                            for (int i = 0; i < 3; i++)
                                fData3[i] = (float) (fData3[i] / 32768.0 * 16.0);
                            for (int i = 3; i < 6; i++)
                                fData3[i] = (float) (fData3[i] / 32768.0 * 2000.0);
                            for (int i = 6; i < 9; i++)
                                fData3[i] = (float) (fData3[i] / 32768.0 * 180.0);
                            strIMU3 = (String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",fData3[0], fData3[1], fData3[2],fData3[3], fData3[4], fData3[5],fData3[6], fData3[7], fData3[8]));
                            //strIMU3 = (String.format("%.2f %.2f %.2f ", fData3[6], fData3[7], fData3[8]));
                            //strIMU3 = (String.format("%.2f %.2f %.2f ", 0.3, 0.3, 0.3));
                            intent3.putExtra(EXTRA_DATA3, mBluetoothDeviceAddress + "," + strIMU3);
                            sendBroadcast(intent3);
//                            if(SConnected1 && SConnected2 && SConnected3 && SConnected4 && SConnected5)
//                            {
//                                dataCache3.add(new dataVector(fData3[0], fData3[1], fData3[2],fData3[3], fData3[4], fData3[5],fData3[6], fData3[7], fData3[8]));
//                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case SensorFour:
                final Intent intent4 = new Intent(action);
                final byte[] packBuffer4 = characteristic.getValue();
                float[] fData4 = new float[9];
                if (packBuffer4 != null && packBuffer4.length == 20) {
                    switch (packBuffer4[1]) {
                        case 0x61:
                            for (int i = 0; i < 9; i++)
                                fData4[i] = (((short) packBuffer4[i * 2 + 3]) << 8) | ((short) packBuffer4[i * 2 + 2] & 0xff);
                            for (int i = 0; i < 3; i++)
                                fData4[i] = (float) (fData4[i] / 32768.0 * 16.0);
                            for (int i = 3; i < 6; i++)
                                fData4[i] = (float) (fData4[i] / 32768.0 * 2000.0);
                            for (int i = 6; i < 9; i++)
                                fData4[i] = (float) (fData4[i] / 32768.0 * 180.0);
                            //strIMU4 = (String.format("%.2f %.2f %.2f ", fData4[6], fData4[7], fData4[8]));
                            strIMU4 =(String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",fData4[0], fData4[1], fData4[2],fData4[3], fData4[4], fData4[5],fData4[6], fData4[7], fData4[8]));
                            intent4.putExtra(EXTRA_DATA4, mBluetoothDeviceAddress + "," + strIMU4);
                            sendBroadcast(intent4);
//                            if(SConnected1 && SConnected2 && SConnected3 && SConnected4 && SConnected5)
//                            {
//                                dataCache4.add(new dataVector( fData4[0], fData4[1], fData4[2],fData4[3], fData4[4], fData4[5],fData4[6], fData4[7], fData4[8]));
//                            }
                            //mBluetoothDeviceAddress=SensorOne;
                            break;
                        default:
                            break;
                    }
                }
                break;
            case SensorFive:
                final Intent intent5 = new Intent(action);
                final byte[] packBuffer5 = characteristic.getValue();
                float[] fData5 = new float[9];
                if (packBuffer5 != null && packBuffer5.length == 20) {
                    switch (packBuffer5[1]) {
                        case 0x61:
                            for (int i = 0; i < 9; i++)
                                fData5[i] =(((short)packBuffer5[i*2+3])<<8)|((short)packBuffer5[i*2+2]&0xff);
                            for (int i = 0; i < 3; i++)
                                fData5[i] = (float) (fData5[i] / 32768.0 * 16.0);
                            for (int i = 3; i < 6; i++)
                                fData5[i] = (float) (fData5[i] / 32768.0 * 2000.0);
                            for (int i = 6; i < 9; i++)
                                fData5[i] = (float) (fData5[i] / 32768.0 * 180.0);
                            strIMU5 =(String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",fData5[0], fData5[1], fData5[2],fData5[3], fData5[4], fData5[5],fData5[6], fData5[7], fData5[8]));
                            //strIMU5 = (String.format("%.2f %.2f %.2f ", fData5[6], fData5[7], fData5[8]));
                            intent5.putExtra(EXTRA_DATA5, mBluetoothDeviceAddress + "," + strIMU5);
                            sendBroadcast(intent5);

                            //mBluetoothDeviceAddress=SensorOne;
                            break;
                        default:
                            break;
                    }
                }
                break;
            //strIMU =(String.format("Normal\r\na:%.2f %.2f %.2f \r\nw:%.2f %.2f %.2f \r\nA:%.0f %.0f %.0f ",fData[0],fData[1],fData[2],fData[3],fData[4],fData[5],fData[6],fData[7],fData[8]));
            //intent.putExtra(EXTRA_DATA, "RSSI  :"+strIMU);

        }
    }
    public byte DataCheck(byte byteIO,String strACK,float fPressrue,float [] fIMU)
    {
        byte byteResult = 0;
        if (byteIO!=0x0f) byteResult = -2;
        if (strACK.compareTo("Text:wtzn\r\n")!=0) byteResult = -6;
        double a = Math.sqrt(fIMU[0]*fIMU[0]+fIMU[1]*fIMU[1]+fIMU[2]*fIMU[2]);
        double w = Math.sqrt(fIMU[3]*fIMU[3]+fIMU[4]*fIMU[4]+fIMU[5]*fIMU[5]);
        double h = Math.sqrt(fIMU[6]*fIMU[6]+fIMU[7]*fIMU[7]+fIMU[8]*fIMU[8]);
        if ((a<0.6)|(a>1.4)) byteResult = -5;
        if ((w>500)|(w==0)) byteResult = -4;
        if ((h>1000)|(h<10)) byteResult = -3;
        if (fPressrue<90000) byteResult = -1;
        return  byteResult;
    }
    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic
                    .getDescriptor(UUID
                            .fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    public boolean writeByes(byte[] bytes) {
        if (mNotifyCharacteristic != null) {
            Log.d("BLE","WriteByte");
            mNotifyCharacteristic.setValue(bytes);
            return mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
        } else {
            Log.d("BLE","NOCharacter");
            return false;
        }
    }

    public boolean writeString(String s) {
        if (mNotifyCharacteristic != null) {
            mNotifyCharacteristic.setValue(s);
            return mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
        } else {
            return false;
        }
    }

   private void getWorkableGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.toLowerCase().contains("ffe9")) {//Write
                    mNotifyCharacteristic = gattCharacteristic;
                    setCharacteristicNotification(mNotifyCharacteristic, true);
                }
                if (uuid.toLowerCase().contains("ffe4")) {//Read
                    setCharacteristicNotification(gattCharacteristic, true);
                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(CCCD);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
    }




    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID DATA_UUID = UUID.fromString("0000ffe5-0000-1000-8000-00805f9634fb");
    public static final UUID WRITE_UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9634fb");
    public static final UUID READ_UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9634fb");

}
