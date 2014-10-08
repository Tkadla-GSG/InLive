package cz.inlive.inlive.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tkadla on 8. 10. 2014.
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent updateIntent = new Intent(context, UpdateIntentService.class);
        context.startService(updateIntent);

    }
}
