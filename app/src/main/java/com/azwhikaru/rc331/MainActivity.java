package com.azwhikaru.rc331;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends Activity {
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private Button bootloader;
    private Button normal;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        bootloader = new Button(this);
        bootloader.setText(R.string.bootloader);
        normal = new Button(this);
        normal.setText(R.string.normal);
//        for (Button button : new Button[]{bootloader, normal}) {
        for (Button button : new Button[]{normal, bootloader}) {
            button.setAllCaps(false);
            layout.addView(button, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        setContentView(layout);
        worker.execute(RootCommands::apply);
        bootloader.setOnClickListener(view -> reboot(RootCommands::rebootBootloader));
        normal.setOnClickListener(view -> reboot(RootCommands::rebootNormal));
    }

    private void reboot(Runnable command) {
        bootloader.setEnabled(false);
        normal.setEnabled(false);
        worker.execute(() -> {
            try {
                command.run();
            } finally {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        bootloader.setEnabled(true);
                        normal.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override protected void onDestroy() {
        worker.shutdown();
        super.onDestroy();
    }
}
