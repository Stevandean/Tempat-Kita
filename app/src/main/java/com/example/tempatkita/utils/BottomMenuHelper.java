package com.example.tempatkita.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tempatkita.R;
import com.example.tempatkita.ui.AddWisataActivity;
import com.example.tempatkita.ui.LoginActivity;
import com.example.tempatkita.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class BottomMenuHelper {

    public static void setup(Activity activity) {

        LinearLayout btnHome  = activity.findViewById(R.id.btnHome);
        LinearLayout btnFav   = activity.findViewById(R.id.btnFav);
        LinearLayout btnLogin = activity.findViewById(R.id.btnLogin);
        LinearLayout btnUser  = activity.findViewById(R.id.btnUser);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            // ===================== GUEST =====================
            show(btnHome, btnLogin);
            hide(btnFav, btnUser);

            setLabel(btnHome, "Home", R.drawable.ic_home);
            setLabel(btnLogin, "Login", R.drawable.ic_user);

            btnHome.setOnClickListener(v ->
                    safeOpen(activity, MainActivity.class));

            btnLogin.setOnClickListener(v ->
                    activity.startActivity(new Intent(activity, LoginActivity.class)));

        } else {
            // ===================== LOGIN =====================
            String role = activity
                    .getSharedPreferences("USER_DATA", Activity.MODE_PRIVATE)
                    .getString("role", "user");

            show(btnHome, btnUser);
            hide(btnLogin);

            setLabel(btnUser, "Logout", R.drawable.ic_user);
            btnUser.setOnClickListener(v -> doLogout(activity));

            // ===================== HOME (SEMUA ROLE) =====================
            setLabel(btnHome, "Home", R.drawable.ic_home);
            btnHome.setOnClickListener(v ->
                    safeOpen(activity, MainActivity.class));

            if ("admin".equals(role)) {
                // ===================== ADMIN =====================
                show(btnFav);

                setLabel(btnFav, "Kelola", R.drawable.ic_home);
                btnFav.setOnClickListener(v ->
                        safeOpen(activity, AddWisataActivity.class));

            } else {
                // ===================== USER =====================
                show(btnFav);

                setLabel(btnFav, "Favorite", R.drawable.ic_favorite);
                btnFav.setOnClickListener(v ->
                        Toast.makeText(activity,
                                "Fitur Favorite belum tersedia",
                                Toast.LENGTH_SHORT).show());
            }
        }
    }

    // ===================== LOGOUT =====================
    private static void doLogout(Activity activity) {

        FirebaseAuth.getInstance().signOut();

        SharedPreferences prefs =
                activity.getSharedPreferences("USER_DATA", Activity.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);

        Toast.makeText(activity, "Berhasil logout", Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    // ===================== UTIL =====================
    private static void show(View... views) {
        for (View v : views) v.setVisibility(View.VISIBLE);
    }

    private static void hide(View... views) {
        for (View v : views) v.setVisibility(View.GONE);
    }

    private static void setLabel(LinearLayout btn, String text, int iconRes) {
        ImageView icon = (ImageView) btn.getChildAt(0);
        TextView label = (TextView) btn.getChildAt(1);
        icon.setImageResource(iconRes);
        label.setText(text);
    }

    private static void safeOpen(Activity from, Class<?> to) {
        if (!from.getClass().equals(to)) {
            from.startActivity(new Intent(from, to));
        }
    }
}
