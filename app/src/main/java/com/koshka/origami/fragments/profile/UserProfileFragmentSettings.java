package com.koshka.origami.fragments.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.koshka.origami.R;
import com.koshka.origami.activites.login.LoginActivity;
import com.koshka.origami.activites.profile.settings.about.AboutUsActivity;
import com.koshka.origami.activites.profile.settings.about.FAQActivity;
import com.koshka.origami.activites.profile.settings.about.LicencesActivity;
import com.koshka.origami.activites.profile.settings.about.ToSActivity;
import com.koshka.origami.activites.profile.settings.account.ChangeEmailActivity;
import com.koshka.origami.activites.profile.settings.account.ChangePasswordActivity;
import com.koshka.origami.activites.profile.settings.account.DeleteAccountActivity;
import com.koshka.origami.activites.profile.settings.application.UISettingsActivity;
import com.koshka.origami.activites.profile.settings.application.NotificationsActivity;
import com.koshka.origami.fragments.GenericOrigamiFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.koshka.origami.utils.SharedPrefs.SHARED_PREFS;

/**
 * Created by imuntean on 8/6/16.
 */
public class UserProfileFragmentSettings extends GenericOrigamiFragment {

    private static final String TAG = "SettingsFragment";

    //----------------------------------------------------------------------------------------------

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.origami_text_logo)
    TextView origamiTextLogo;

    //----------------------------------------------------------------------------------------------

    private static final int UI_PREFS_REQUEST = 2;
    SweetAlertDialog dialog;

    //----------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/origamibats.ttf");
        origamiTextLogo.setTypeface(font);

    }

    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.sign_out)
    public void signOut() {

        Resources res = getResources();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(res.getString(R.string.log_out_warning))
                .setPositiveButton(res.getString(R.string.positive_log_out), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOutFromFirebase();
                    }
                })
                .setNegativeButton(res.getString(R.string.cancel), null)
                .create();

        dialog.show();

    }

    private void signOutFromFirebase() {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(LoginActivity.createIntent(getActivity()));
                            getActivity().finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }


    @OnClick({R.id.change_email_textview, R.id.change_password_textview})
    public void changeEmail(TextView view) {
        switch (view.getId()) {
            case R.id.change_email_textview: {
                startMyActivity(ChangeEmailActivity.class);
                break;
            }
            case R.id.change_password_textview: {
                startMyActivity(ChangePasswordActivity.class);
                break;
            }
            default:
                break;
        }

    }

    @OnClick({R.id.notifications_settings_text_button, R.id.blocked_users_text_button, R.id.connect_with_facebook_text_button, R.id.design_settings_text_button})
    public void changeAppSettings(TextView view) {
        switch (view.getId()) {
            case R.id.notifications_settings_text_button: {
                startMyActivity(NotificationsActivity.class);
                break;
            }
            case R.id.blocked_users_text_button: {
                break;
            }
            case R.id.connect_with_facebook_text_button: {
                break;
            }
            case R.id.design_settings_text_button: {
                Intent intent = new Intent(getActivity(), UISettingsActivity.class);
                startActivityForResult(intent, UI_PREFS_REQUEST);

                break;
            }
            default:
                break;
        }

    }

    @OnClick({R.id.about_us_text_button, R.id.tos_text_button, R.id.faq_text_button, R.id.licences_text_button})
    public void startAboutActivity(TextView view) {

        switch (view.getId()) {
            case R.id.about_us_text_button: {
                startMyActivity(AboutUsActivity.class);
                break;
            }
            case R.id.tos_text_button: {
                startMyActivity(ToSActivity.class);
                break;
            }
            case R.id.faq_text_button: {
                startMyActivity(FAQActivity.class);
                break;
            }
            case R.id.licences_text_button: {
                startMyActivity(LicencesActivity.class);
                break;
            }
            default:
                break;
        }
    }

    private void startMyActivity(Class clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }


    @OnClick(R.id.delete_account)
    public void deleteAccountClicked() {

        startMyActivity(DeleteAccountActivity.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == UI_PREFS_REQUEST) {
            SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            int backgroundGradient = prefs.getInt("gradient", -1);
            if (backgroundGradient != -1){
                getActivity().getWindow().setBackgroundDrawable(getResources().getDrawable(backgroundGradient));
            }

        }

    }

}