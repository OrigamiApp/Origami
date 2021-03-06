package com.koshka.origami.activites.profile.settings.account;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.ui.email.field_validators.EmailFieldValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.koshka.origami.R;
import com.koshka.origami.activites.OrigamiActivity;
import com.koshka.origami.activites.profile.settings.account.validators.CurrentPasswordFieldValidator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by imuntean on 8/11/16.
 */
public class ChangeEmailActivity extends OrigamiActivity {

    private static final String TAG = "ChangeEmailActivity";

    //----------------------------------------------------------------------------------------------

    @BindView(R.id.toolbar_email_change)
    Toolbar toolbar;

    @BindView(R.id.current_password_for_email_layout)
    TextInputLayout currentPasswordLayout;

    @BindView(R.id.email_change_text_layout)
    TextInputLayout emailChangeLayout;

    @BindView(R.id.change_email_edit_text)
    EditText changeEmailEditText;

    @BindView(R.id.current_email_text_view)
    TextView currentEmailTextView;

    @BindView(R.id.current_password_edit_text)
    EditText currentPasswordEdit;

    @BindView(R.id.button_change_email_next)
    Button nextButton;

    @BindView(R.id.button_change_email)
    Button changeEmailButton;

    //----------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_email_activity);
        ButterKnife.bind(this);

        currentEmailTextView.setText(activityHelper.getCurrentUser().getEmail());

        setSupportActionBar(toolbar);


        clearPasswords();
    }

    private void clearPasswords() {

        if (currentPasswordEdit != null) {
            currentPasswordEdit.setText("");
        }
    }

    @OnClick(R.id.button_change_email_next)
    public void next(View view) {

        CurrentPasswordFieldValidator validator = new CurrentPasswordFieldValidator(currentPasswordLayout, 6);
        String currentPassword = currentPasswordEdit.getText().toString();
        boolean isCurrentPasswordValid = validator.validate(currentPassword);

        if (isCurrentPasswordValid) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        currentPasswordLayout.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        emailChangeLayout.setVisibility(View.VISIBLE);
                        changeEmailButton.setVisibility(View.VISIBLE);


                    } else {
                        currentPasswordLayout.setError("Password is incorrect");
                    }
                }
            });
        }
    }

    @OnClick(R.id.button_change_email)
    public void changeEmail(View view) {


        final SweetAlertDialog successDialog = new SweetAlertDialog(this)
                .setTitleText("Success!")
                .setContentText("Your password was changed")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        activityHelper.goHome();
                    }
                });

        successDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);


        final String changedEmail = changeEmailEditText.getText().toString();
        EmailFieldValidator validator = new EmailFieldValidator(emailChangeLayout);


        boolean emailIsValid = validator.validate(changedEmail);

        if (emailIsValid) {

            Resources res = getResources();
            String email = currentUser.getEmail();
            String password = currentPasswordEdit.getText().toString();
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "User re-authenticated.");
                        String updatedEmail = changeEmailEditText.getText().toString();
                        if (updatedEmail != null && !updatedEmail.isEmpty()) {
                            currentUser.updateEmail(updatedEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email was updated.");
                                        successDialog.show();
                                        currentEmailTextView.setText(currentUser.getEmail());
                                    } else {
                                        Log.d(TAG, "Email was not updated.");
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "User was not re-authenticated.");
                    }

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activityHelper.goHome();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
