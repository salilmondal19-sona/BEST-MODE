package com.beast.launcher;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.content.pm.*;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout root;
    ListView appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 35, 20, 20);
        root.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("BEAST");
        title.setTextSize(38);
        title.setTextColor(Color.RED);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("SYSTEM LAUNCHER");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);

        TextView clock = new TextView(this);
        clock.setText(new java.text.SimpleDateFormat(
            "hh:mm a  |  dd MMM",
            java.util.Locale.getDefault()
        ).format(new Date()));
        clock.setTextSize(22);
        clock.setTextColor(Color.WHITE);
        clock.setGravity(Gravity.CENTER);

        appList = new ListView(this);
        appList.setBackgroundColor(Color.BLACK);

        root.addView(title);
        root.addView(subtitle);
        root.addView(clock);
        root.addView(appList);

        setContentView(root);

        loadApps();
    }

    private void loadApps() {

        PackageManager pm = getPackageManager();

        List<ApplicationInfo> apps =
            pm.getInstalledApplications(0);

        Collections.sort(apps, (a, b) ->
            pm.getApplicationLabel(a).toString()
            .compareToIgnoreCase(
                pm.getApplicationLabel(b).toString()
            )
        );

        ArrayList<ApplicationInfo> launchable =
            new ArrayList<>();

        ArrayList<String> names = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                launchable.add(app);
                names.add(pm.getApplicationLabel(app).toString());
            }
        }

        ArrayAdapter<String> adapter =
            new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                names
            ) {
                @Override
                public View getView(int position,
                    View convertView, android.view.ViewGroup parent) {

                    TextView view = (TextView) super.getView(
                        position, convertView, parent
                    );

                    view.setTextColor(Color.WHITE);
                    view.setTextSize(17);
                    view.setPadding(20, 18, 10, 18);

                    return view;
                }
            };

        appList.setAdapter(adapter);

        appList.setOnItemClickListener((parent, view, position, id) -> {
            String pkg = launchable.get(position).packageName;
            Intent intent = pm.getLaunchIntentForPackage(pkg);

            if (intent != null) {
                startActivity(intent);
            }
        });
    }
}

