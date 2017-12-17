package utils;

import android.content.Context;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Li Wenzhao on 2017/12/12.
 */

public class ToastUtils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context,msg, LENGTH_SHORT).show();
    }
}
