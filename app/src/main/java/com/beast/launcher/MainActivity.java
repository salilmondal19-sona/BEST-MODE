package com.beast.launcher;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.text.*;
import android.content.*;
import android.content.pm.*;
import java.util.*;

public class MainActivity extends Activity {

    ListView appList;
    ArrayList<ApplicationInfo> allApps = new ArrayList<>();
    ArrayList<ApplicationInfo> filteredApps = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super
cd ~/BEAST-LAUNCHER

cat > app/src/main/java/com/beast/launcher/MainActivity.java <<'EOF'
package com.beast.launcher;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import android.text.*;
import android.content.*;
import android.content.pm.*;
import java.util.*;

public class MainActivity extends Activity {

    ListView appList;
    ArrayList<ApplicationInfo> allApps = new ArrayList<>();
    ArrayList<ApplicationInfo> filteredApps = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
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

        EditText search = new EditText(this);
        search.setHint("Search Apps...");
        search.setHintTextColor(Color.GRAY);
        search.setTextColor(Color.WHITE);
        search.setSingleLine(true);

        appList = new ListView(this);
        appList.setBackgroundColor(Color.BLACK);

        root.addView(title);
        root.addView(subtitle);
        root.addView(search);
        root.addView(appList);

        setContentView(root);

        loadApps();

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }
            public void afterTextChanged(Editable e) {}
        });

        appList.setOnItemClickListener((parent, view, position, id) -> {
            ApplicationInfo app = filteredApps.get(position);
            Intent intent = getPackageManager()
                .getLaunchIntentForPackage(app.packageName);
            if (intent != null) startActivity(intent);
        });
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();

        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                allApps.add(app);
            }
        }

        Collections.sort(allApps, (a, b) ->
            pm.getApplicationLabel(a).toString()
            .compareToIgnoreCase(pm.getApplicationLabel(b).toString())
        );

        filteredApps.addAll(allApps);
        filterApps("");
    }

    private void filterApps(String query) {
        PackageManager pm = getPackageManager();
        names.clear();
        filteredApps.clear();

        for (ApplicationInfo app : allApps) {
            String name = pm.getApplicationLabel(app).toString();

            if (name.toLowerCase().contains(query.toLowerCase())) {
                names.add(name);
                filteredApps.add(app);
            }
        }

        adapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, names
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                view.setTextSize(17);
                view.setPadding(20, 18, 10, 18);
                return view;
            }
        };

        appList.setAdapter(adapter);
    }
}
