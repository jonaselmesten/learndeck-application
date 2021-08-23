package activity;

import android.content.Context;
import android.widget.Toast;

public class UiUtil {

    private UiUtil() {}

    /**
     * Shows a message on the GUI.
     * @param context Application context.
     * @param message Message to show.
     */
    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context,
                "Connection error - Couldn't reset.",
                Toast.LENGTH_SHORT);
        toast.show();
    }

}
