package com.spryshanto.cr_alarm;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_USE_EXACT_ALARM = 109;
    private static final int REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnBatteryOptimization = findViewById(R.id.btnBatteryOptimization);
        Button btnNotificationPermission = findViewById(R.id.btnNotificationPermission);
        Button btnAlarmPermission = findViewById(R.id.btnAlarmPermission);
        Button btnUseExactAlarm = findViewById(R.id.btnUseExactAlarm);
        Button btnSystemAlertWindowPermission = findViewById(R.id.btnSystemAlertWindowPermission);

        Button btnStartBackgroundTask = findViewById(R.id.btnStartBackgroundTask);
        Button btnStartBackgroundTaskWorker = findViewById(R.id.btnStartBackgroundTaskWorker);
        Button btnCancelBackgroundTaskWorker = findViewById(R.id.btnCancelBackgroundTaskWorker);
        Button btnCancelBackgroundTaskAlarmManager = findViewById(R.id.btnCancelBackgroundTaskAlarmManager);

        btnBatteryOptimization.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("BatteryLife")
            @Override
            public void onClick(View v) {
                // Request battery optimization exemption
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                // Check if battery optimization is ignored
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(getPackageName());

                if (isIgnoringBatteryOptimizations) {
                    // Battery optimization is granted, handle accordingly
                    Toast.makeText(MainActivity.this, "IGNORE_BATTERY_OPTIMIZATIONS permission is granted on your device.", Toast.LENGTH_SHORT).show();
                } else {
                    // Battery optimization is not ignored, inform the user or take appropriate action
                    Toast.makeText(MainActivity.this, "IGNORE_BATTERY_OPTIMIZATIONS permission is not granted on your device.", Toast.LENGTH_SHORT).show();
                }
                Log.d("BatteryOptimization", "zzzzzz BatteryOptimization");
            }
        });

        btnNotificationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request notification permission
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                }
                else {
                    // Inform the user that this feature is not available on their device
                    Toast.makeText(MainActivity.this, "Notification permission is automatically granted on your device.", Toast.LENGTH_SHORT).show();
                }
                Log.d("NotificationPermission", "zzzzzz NotificationPermission");
            }
        });

        btnAlarmPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request alarm permission (You may need to customize this part)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                else {
                    // Inform the user that this feature is not available on their device
                    Toast.makeText(MainActivity.this, "SCHEDULE_EXACT_ALARM permission is automatically granted on your device.", Toast.LENGTH_SHORT).show();
                }
                Log.d("AlarmPermission", "zzzzzz AlarmPermission SCHEDULE_EXACT_ALARM");
            }
        });

        btnUseExactAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request USE_EXACT_ALARM permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermission(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            REQUEST_USE_EXACT_ALARM
                    );
                }
                else {
                    // Inform the user that this feature is not available on their device
                    Toast.makeText(MainActivity.this, "USE_EXACT_ALARM permission is not available on your device.", Toast.LENGTH_SHORT).show();
                }
                Log.d("AlarmPermission", "zzzzzz AlarmPermission");
            }
        });

        btnSystemAlertWindowPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request SYSTEM_ALERT_WINDOW permission
                requestPermission(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION
                );
                Log.d("SystemAlertWindowPermission", "zzzzzz SystemAlertWindowPermission");}
        });


        btnStartBackgroundTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start periodic background task
                schedulePeriodicTaskUsingAlarmManager();
                Log.d("StartBackgroundTask", "zzzzzz StartBackgroundTask");
            }
        });

        btnStartBackgroundTaskWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start periodic background task
                schedulePeriodicTaskUsingWorkManager();
                Log.d("StartBackgroundTask", "zzzzzz StartBackgroundTask");
            }
        });

        btnCancelBackgroundTaskWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start periodic background task
                cancelSchedulePeriodicTaskUsingWorkManager();
                Log.d("CancelBackgroundTaskWorker", "zzzzzz CancelBackgroundTaskWorker");
            }
        });

        btnCancelBackgroundTaskAlarmManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start periodic background task
                cancelSchedulePeriodicTaskUsingAlarmManager();
                Log.d("CancelBackgroundTaskAlarmManager", "zzzzzz CancelBackgroundTaskAlarmManager");
            }
        });
    }




    // Request permissions that require a specific action or intent
    private void requestPermission(String action, int requestCode) {
        Intent intent = new Intent(action);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    // Handle permission request response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SYSTEM_ALERT_WINDOW_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission granted
                Toast.makeText(MainActivity.this, "SYSTEM_ALERT_WINDOW permission granted", Toast.LENGTH_SHORT).show();
                Log.d("PermissionRequest", "SYSTEM_ALERT_WINDOW permission granted");
            } else {
                // SYSTEM_ALERT_WINDOW permission denied
                Toast.makeText(MainActivity.this, "SYSTEM_ALERT_WINDOW permission denied", Toast.LENGTH_SHORT).show();
                Log.d("PermissionRequest", "SYSTEM_ALERT_WINDOW permission denied");
            }
        } else if (requestCode == REQUEST_USE_EXACT_ALARM) {
            if (resultCode == RESULT_OK) {
                // USE_EXACT_ALARM permission granted
                Toast.makeText(MainActivity.this, "USE_EXACT_ALARM permission granted", Toast.LENGTH_SHORT).show();
                Log.d("PermissionRequest", "USE_EXACT_ALARM permission granted");
            } else {
                // USE_EXACT_ALARM permission denied
                Toast.makeText(MainActivity.this, "USE_EXACT_ALARM permission denied", Toast.LENGTH_SHORT).show();
                Log.d("PermissionRequest", "USE_EXACT_ALARM permission denied");
            }
        }
    }



    private void schedulePeriodicTaskUsingAlarmManager() {
        Log.d("schedulePeriodicTaskUsingAlarmManager", "zzzzzz schedulePeriodicTaskUsingAlarmManager");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        long intervalMillis = 60 * 1000; // 1 minutes
        long initialMillis = System.currentTimeMillis();
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                initialMillis,
                intervalMillis,
                pendingIntent
        );
        Toast.makeText(MainActivity.this, "Started Periodic Task Alarm-manager", Toast.LENGTH_SHORT).show();

    }

    private void schedulePeriodicTaskUsingWorkManager() {
        Log.d("schedulePeriodicTaskUsingWorkManager", "zzzzzz schedulePeriodicTaskUsingWorkManager");
        // Define a periodic work request to execute your background task
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class,
                16,
                TimeUnit.MINUTES).addTag("my_unique_tag") // Assign a unique tag
                .build();
        // Enqueue the work request with WorkManager
        WorkManager.getInstance(MainActivity.this).enqueue(workRequest);
        Toast.makeText(MainActivity.this, "Started Periodic Task Work-manager", Toast.LENGTH_SHORT).show();

    }

    private void cancelSchedulePeriodicTaskUsingWorkManager() {
        WorkManager.getInstance(MainActivity.this).cancelAllWorkByTag("my_unique_tag");
        Toast.makeText(MainActivity.this, "Canceled Periodic Task Work-manager", Toast.LENGTH_SHORT).show();
        Log.d("cancelSchedulePeriodicTaskUsingWorkManager", "zzzzzz cancelSchedulePeriodicTaskUsingWorkManager");
    }

    private void cancelSchedulePeriodicTaskUsingAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        // Cancel the previously scheduled alarm
        alarmManager.cancel(pendingIntent);
        Toast.makeText(MainActivity.this, "Canceled Periodic Task Alarm-manager", Toast.LENGTH_SHORT).show();
        Log.d("cancelSchedulePeriodicTaskUsingAlarmManager", "zzzzzz cancelSchedulePeriodicTaskUsingAlarmManager");
    }
}
