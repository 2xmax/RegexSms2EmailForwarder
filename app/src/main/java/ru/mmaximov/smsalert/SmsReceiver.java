package ru.mmaximov.smsalert;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import ru.mmaximov.smsalert.biz.Message;
import ru.mmaximov.smsalert.biz.MessageFilter;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    final Object[] protocolDescriptionUnits = (Object[]) bundle.get("pdus");
                    for (Object protocolDescriptionUnit : protocolDescriptionUnits) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) protocolDescriptionUnit);
                        Message msg = new Message(sms.getDisplayOriginatingAddress(), sms.getDisplayMessageBody());
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        MessageFilter filter = new MessageFilter(
                                prefs.getString("filter_phone", null),
                                prefs.getString("filter_text", null),
                                "and".equals(prefs.getString("filter_condition", null)) ? MessageFilter.FilterType.And : MessageFilter.FilterType.Or);
                        if (filter.matches(msg)) {
                            new EmailNotifier(context).notify(msg);
                        }
                    }
                } catch (Exception ex) {
                    Log.e("SmsReceiver", "Exception smsReceiver" + ex);
                }
            }
        }
    }
}
