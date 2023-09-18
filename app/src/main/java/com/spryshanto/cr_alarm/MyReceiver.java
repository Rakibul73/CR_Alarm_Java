package com.spryshanto.cr_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "MyPrefsFile2"; // Name of the preferences file
    private static final String LAST_TIMESTAMP_KEY = "lastTimestamp2"; // Key for storing the last timestamp

    @Override
    public void onReceive(Context context, Intent intent) {
        // Perform your periodic task here, e.g., fetch data from the API
        Log.d("MyReceiver", "zzzzzz onReceive");
        new FetchDataAsyncTask(context).execute();
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context; // Store the context here
        public FetchDataAsyncTask(Context context) {
            this.context = context; // Initialize the context in the constructor
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("MyReceiver", "zzzzzz doInBackground");
            fetchFromApi(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Handle post-execution tasks if needed
        }
    }

    private void fetchFromApi(Context context) {
        Log.d("MyReceiver", "zzzzzz fetchFromApi");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://tuimorsala.pythonanywhere.com/get_timestamp") // Replace with your API endpoint
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String jsonResponse = responseBody.string();
                    // Handle the JSON response
                    handleJsonResponse(context , jsonResponse);
                }
            } else {
                // Handle error response
                Log.e("YourBackgroundService", "API request failed");
            }
        } catch (IOException e) {
            // Handle exceptions
            Log.e("YourBackgroundService", "Exception: " + e.getMessage());
        }
    }

    private void handleJsonResponse(Context context , String jsonResponse) {
        Log.d("YourBackgroundService", "zzzzzz handleJsonResponse");
        try {
            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String text = jsonObject.getString("text");
            String newTimestamp = jsonObject.getString("timestamp");
            // Now you can use 'text' and 'timestamp' as needed
            Log.d("YourBackgroundService", "zzzzzz Text: " + text);
            Log.d("YourBackgroundService", "zzzzzz Timestamp: " + newTimestamp);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Log.d("YourBackgroundService", "zzzzzz Current Date: " + dateFormat.format(date));

            // Fetch the last saved timestamp
            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String lastTimestamp = sharedPreferences.getString(LAST_TIMESTAMP_KEY, "");
            // Check if the new timestamp is different from the last one
            if (!newTimestamp.equals(lastTimestamp)) {
                // Timestamp has changed, save the new timestamp
                // Save the new timestamp in SharedPreferences
                sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(LAST_TIMESTAMP_KEY, newTimestamp);
                editor.apply();
                // For example, display a notification or update UI
                setCustomAlarmWithIntent(context , text, newTimestamp);
            } else {
                // Timestamp has not changed, no need to reschedule the task
                Log.d("YourBackgroundService", "zzzzzz No need to reschedule the task");
            }

        } catch (JSONException e) {
            Log.e("YourBackgroundService", "zzzzzz JSON parsing error: " + e.getMessage());
        }
    }

    private void setCustomAlarmWithIntent(Context context , String text , String timestamp){
        Log.d("setCustomAlarmWithIntent", "zzzzzz setCustomAlarmWithIntent");
        try {
            Log.d("text", "zzzzzz text");
            // Define a SimpleDateFormat for parsing the timestamp
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
            // Parse the timestamp string into a Date object
            Date date = inputDateFormat.parse(timestamp);
            // Define a SimpleDateFormat for formatting the date in 24-hour format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            // Format the date to obtain the hours and minutes in 24-hour format
            String formattedTime = outputDateFormat.format(date);
            // Split the formatted time into hours and minutes
            String[] timeParts = formattedTime.split(":");
            // Extract hours and minutes as separate variables
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            // Now, 'hours' and 'minutes' contain the extracted values
            System.out.println("zzzzzz Hours: " + hours);
            System.out.println("zzzzzz Minutes: " + minutes);
            // Get the current time in hours and minutes
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
            int currentMinute = calendar.get(Calendar.MINUTE);
            Log.d("setCustomAlarmWithIntent", "zzzzzz Current Time = " + currentHour + ":" + currentMinute);
            // add 2 minutes in current minute
            currentMinute += 3;
            // set alarm with intent in default alarm clock mode
            setAlarmWithIntentDefaultAlarmClock(context , currentHour , currentMinute);
        } catch (Exception e) {
            Log.e("YourBackgroundService", "zzzzzz JSON parsing error: " + e.getMessage());
        }
    }


    private void setAlarmWithIntentDefaultAlarmClock(Context context, int currentHour, int currentMinute) {
        // Create an intent to set an alarm using android.intent.action.SET_ALARM
        Intent intent = new Intent("android.intent.action.SET_ALARM");

        // Specify alarm details
        intent.putExtra("android.intent.extra.alarm.MESSAGE", "CR Alarm");
        intent.putExtra("android.intent.extra.alarm.HOUR", currentHour); // Hour (24-hour format)
        intent.putExtra("android.intent.extra.alarm.MINUTES", currentMinute); // Minutes
        intent.putExtra("android.intent.extra.alarm.SKIP_UI", true); // Set to true to skip the alarm app's UI
        intent.putExtra("android.intent.extra.alarm.VIBRATE", true); // Set to true to vibrate

        // Specify the days of the week for a repeating alarm
//        ArrayList<Integer> daysOfWeek = new ArrayList<>();
//        daysOfWeek.add(Calendar.SUNDAY);
//        daysOfWeek.add(Calendar.TUESDAY);
//        daysOfWeek.add(Calendar.THURSDAY);
//        daysOfWeek.add(Calendar.SATURDAY);
//        daysOfWeek.add(Calendar.MONDAY);
//        daysOfWeek.add(Calendar.WEDNESDAY);
//        daysOfWeek.add(Calendar.FRIDAY);
//        intent.putExtra("android.intent.extra.alarm.DAYS", daysOfWeek);
        Log.d("intent", "zzzzzz alarm details");
        // Start the alarm clock app with the intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }

        Log.d("intent", "zzzzzz Start the alarm");
    }
}
