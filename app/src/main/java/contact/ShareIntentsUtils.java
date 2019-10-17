package contact;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import contact.picker.PickedContact;

public class ShareIntentsUtils {

    public static void sendShareTextIntent(AppCompatActivity activity, String message){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Faceter camera sharing");
        //String message = "Привет, я поделился с тобой камерой \"iPhone aaronskiy\", так что можешь использовать её в качестве камеры наблюдения.";

        i.putExtra(Intent.EXTRA_TEXT, message);
        activity.startActivity(Intent.createChooser(i, "Share camera iPhone aaronskiy"));
    }

    public static void sendSmsIntent(AppCompatActivity activity, String number, String message) {
        Uri uriSms = Uri.parse("smsto:" + number);
        Intent intentSMS = new Intent(Intent.ACTION_SENDTO, uriSms);
        //intentSMS.putExtra("sms_body", "Привет, я поделился с тобой камерой \"iPhone aaronskiy\", так что можешь использовать её в качестве камеры наблюдения.");
        intentSMS.putExtra("sms_body", message);
        activity.startActivity(intentSMS);
    }
}
