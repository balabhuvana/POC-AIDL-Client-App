package com.arunv.aidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.arunv.aidlserver.IFirstInterface;
import com.arunv.aidlserver.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection mServiceConnection;
    private IFirstInterface iFirstInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addTwoNumbers();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnCustomObject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    printPersonsList();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("-----> ", "onServiceConnected()");
                iFirstInterface = IFirstInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("-----> ", "onServiceDisconnected()");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        initConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }

    private void initConnection() {
        Intent intent = new Intent(IFirstInterface.class.getName());

        /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
        intent.setAction("service.printname");

        /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
        intent.setPackage("com.arunv.aidlserver");

        // binding to remote service
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);

    }

    private void addTwoNumbers() throws RemoteException {
        Log.i("-----> ", "" + iFirstInterface.add(5, 7));
    }

    private void printPersonsList() throws RemoteException {
        List<Person> personList = iFirstInterface.getPersonList();

        for (int i = 0; i < personList.size(); i++) {
            Log.i("-----> ", "Name : " + personList.get(i).name);
            Log.i("-----> ", "Age : " + personList.get(i).age);
        }
    }
}
