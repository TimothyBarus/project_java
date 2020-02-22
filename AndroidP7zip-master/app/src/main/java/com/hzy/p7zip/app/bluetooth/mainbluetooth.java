package com.hzy.p7zip.app.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hzy.p7zip.app.R;
import com.hzy.p7zip.app.activity.StorageActivity;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.core.content.FileProvider.getUriForFile;

public class mainbluetooth extends AppCompatActivity {
    /**
     * Create Objects
     */
    private static final String TAG = "mainbluetooth";
    EditText dataPath;
    public static final int FILE_REQUEST_CODE = 10;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbluetooth);
        ButterKnife.bind(this);
        if (adapter == null) {
            Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
        }
            dataPath = (EditText) findViewById(R.id.file_path);

            Intent Rintent = getIntent();
            String pathname = Rintent.getStringExtra("pathId");
            dataPath.setText(pathname);
            Log.i("Check3", dataPath.toString());

        }

    @OnClick(R.id.Sendblue)
    public void sendViaBluetooth () {
        if (!dataPath.equals(null)) {
            Toast.makeText(this, "Please select a file.", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }

        /**
        @OnClick(R.id.Sendblue)
        public void sendViaBluetooth () {
            if (!dataPath.equals(null)) {

            } else {
                enableBluetooth();
            }
        } else{
            Toast.makeText(this, "Please select a file.", Toast.LENGTH_LONG).show();
        }
    }
         **/

    @OnClick(R.id.select_file)
    public void btnSelectFile() {
        Intent intent = new Intent(this, StorageActivity.class);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }


    //exit to application---------------------------------------------------------------------------
    @OnClick(R.id.buttonQuit)
    public void Exit() {
        adapter.disable();
        Toast.makeText(this, "*** Now Bluetooth is off... Thanks. ***", Toast.LENGTH_LONG).show();
        finish();
    }


    /**
     * Method for send file via bluetooth
     */

    public void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    /**
     * Override method for sending data via bluetooth availability
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.setType("*/*");
            File file = new File(dataPath.getText().toString());
            Uri fileSend = getUriForFile(mainbluetooth.this, "com.hzy.p7zip.app.provider", file);
            Log.i("Output", fileSend.toString());
            i.putExtra(Intent.EXTRA_STREAM, fileSend);

            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
            if (list.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : list) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }

                /**
                 * CHECK BLUETOOTH available or not
                 */
                if (!found) {
                    Toast.makeText(this, "Bluetooth not been found", Toast.LENGTH_LONG).show();
                } else {
                    i.setClassName(packageName, className);
                    startActivity(i);
                }

            } else {
                Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
