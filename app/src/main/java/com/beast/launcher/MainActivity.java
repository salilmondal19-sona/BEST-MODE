import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.content.*;
import android.content.pm.*;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout root, grid;
    TextView clock;
    ArrayList<ApplicationInfo> apps = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    PackageManager pm;
    GestureDetector detector;

    int RED = Color.RED;
    int WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        pm = getPackageManager();

        detector = new GestureDetector(this,
            new GestureDetector.SimpleOnGestureListener() {

                public boolean onDown(android.view.MotionEvent e) {
                    return true;
                }

                public boolean onFling(
                    android.view.MotionEvent a,
                    android.view.MotionEvent b,
                    float vx, float vy) {

                    float dx = b.getX() - a.getX();
                    float dy = b.getY() - a.getY();

                    if (Math.abs(dy) > Math.abs(dx)) {
                        if (dy < -120) {
                            showDrawer();
                            return true;
                        }
                        if (dy > 120) {
                            showSearch();
                            return true;
                        }
                    }

                    if (Math.abs(dx) > 120) {
                        Toast.makeText(
                            MainActivity.this,
                            dx < 0 ? "NEXT PAGE" : "HOME PAGE",
                            Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }

                    return false;
                }

                public boolean onDoubleTap(android.view.MotionEvent e) {
                    Toast.makeText(
                        MainActivity.this,
                        "BEST MODE",
                        Toast.LENGTH_SHORT
                    ).show();
                    return true;
                }
            });

        loadApps();

        boolean first = getPreferences(0)
            .getBoolean("started", false);

        if (!first) {
            getPreferences(0).edit()
                .putBoolean("started", true).apply();
            showIntro();
        } else {
            showHome();
        }
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent e) {
        detector.onTouchEvent(e);
        return super.dispatchTouchEvent(e);
    }

    private void showIntro() {

        LinearLayout intro = new LinearLayout(this);
        intro.setGravity(Gravity.CENTER);
        intro.setOrientation(1);
        intro.setBackgroundColor(Color.BLACK);

        TextView logo = new TextView(this);
        logo.setText("BEST");
        logo.setTextSize(55);
        logo.setTextColor(RED);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);

        TextView sub = new TextView(this);
        sub.setText("M O D E");
        sub.setTextColor(WHITE);
        sub.setTextSize(20);
        sub.setGravity(Gravity.CENTER);

        TextView powered = new TextView(this);
        powered.setText("POWERED BY BEST-MODE");
        powered.setTextColor(Color.GRAY);
        powered.setGravity(Gravity.CENTER);

        intro.addView(logo);
        intro.addView(sub);
        intro.addView(powered);

        setContentView(intro);

        ScaleAnimation anim = new ScaleAnimation(
            0.3f, 1f, 0.3f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );

        anim.setDuration(1200);
        logo.startAnimation(anim);

        new Handler().postDelayed(() -> showHome(), 2500);
    }

    private void loadApps() {

        List<ApplicationInfo> list =
            pm.getInstalledApplications(0);

        Collections.sort(list, (a, b) ->
            pm.getApplicationLabel(a).toString()
            .compareToIgnoreCase(
                pm.getApplicationLabel(b).toString()
            )
        );

        for (ApplicationInfo app : list) {
            if (pm.getLaunchIntentForPackage(
                app.packageName) != null) {

                apps.add(app);
                names.add(
                    pm.getApplicationLabel(app).toString()
                );
            }
        }
    }

    private GradientDrawable cardBackground() {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(24, 24, 24));
        bg.setCornerRadius(22);
        bg.setStroke(2, RED);
        return bg;
    }

    private void showHome() {

        root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(16, 35, 16, 15);
        root.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("BEST MODE");
        title.setTextSize(36);
        title.setTextColor(RED);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("YOUR GAMING LAUNCHER");
        subtitle.setTextColor(WHITE);
        subtitle.setGravity(Gravity.CENTER);

        clock = new TextView(this);
        clock.setTextSize(28);
        clock.setTextColor(WHITE);
        clock.setGravity(Gravity.CENTER);
        updateClock();

        TextView hint = new TextView(this);
        hint.setText("↑ SWIPE UP FOR ALL APPS");
        hint.setTextColor(RED);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 15, 0, 15);

        ScrollView scroll = new ScrollView(this);

        grid = new LinearLayout(this);
        grid.setOrientation(1);

        scroll.addView(grid);

        root.addView(title);
        root.addView(subtitle);
        root.addView(clock);
        root.addView(hint);
        root.addView(scroll,
            new LinearLayout.LayoutParams(
                -1, 0, 1
            )
        );

        setContentView(root);

        addApps(12);

        Animation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(900);
        root.startAnimation(fade);
    }

    private void addApps(int limit) {

        grid.removeAllViews();

        int count = Math.min(limit, apps.size());

        LinearLayout row = null;

        for (int i = 0; i < count; i++) {

            if (i % 4 == 0) {
                row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER);
                grid.addView(row);
            }

            ApplicationInfo app = apps.get(i);

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(1);
            item.setGravity(Gravity.CENTER);
            item.setBackground(cardBackground());
            item.setPadding(5, 12, 5, 12);

            ImageView icon = new ImageView(this);

            icon.setImageDrawable(pm.getApplicationIcon(app));
            item.addView(icon,
                new LinearLayout.LayoutParams(55, 55));

            TextView name = new TextView(this);
            name.setText(names.get(i));
            name.setTextColor(WHITE);
            name.setTextSize(11);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(1);

            item.addView(name);

            LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, 115, 1);

            params.setMargins(4, 5, 4, 5);
            row.addView(item, params);

            item.setOnClickListener(v -> openApp(app));
        }
    }

    private void showDrawer() {

        root.removeAllViews();

        TextView heading = new TextView(this);
        heading.setText("ALL APPLICATIONS");
        heading.setTextColor(RED);
        heading.setTextSize(27);
        heading.setGravity(Gravity.CENTER);

        Button back = new Button(this);
        back.setText("HOME");
        back.setOnClickListener(v -> showHome());

        root.addView(heading);
        root.addView(back);

        ScrollView scroll = new ScrollView(this);
        grid = new LinearLayout(this);
        grid.setOrientation(1);

        scroll.addView(grid);

        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        addApps(apps.size());
    }

    private void showSearch() {

        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("Search application");

        new AlertDialog.Builder(this)
            .setTitle("BEST MODE SEARCH")
            .setView(input)
            .setPositiveButton("SEARCH", (d, w) -> {

                String query = input.getText()
                    .toString().toLowerCase();

                ArrayList<String> results = new ArrayList<>();
                ArrayList<ApplicationInfo> found =
                    new ArrayList<>();

                for (int i = 0; i < apps.size(); i++) {
                    if (names.get(i).toLowerCase()
                        .contains(query)) {
                        results.add(names.get(i));
                        found.add(apps.get(i));
                    }
                }

                new AlertDialog.Builder(this)
                    .setTitle("SEARCH RESULTS")
                    .setItems(results.toArray(new String[0]),
                        (dialog, position) ->
                            openApp(found.get(position)))
                    .setNegativeButton("CLOSE", null)
                    .show();
            })
            .setNegativeButton("CANCEL", null)
            .show();
    }

    private void openApp(ApplicationInfo app) {

        Intent intent =
            pm.getLaunchIntentForPackage(app.packageName);

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void updateClock() {

        if (clock != null) {
            clock.setText(
                new java.text.SimpleDateFormat(
                    "hh:mm a  |  dd MMM yyyy",
                    Locale.getDefault()
                ).format(new Date())
            );
        }
    }
}

