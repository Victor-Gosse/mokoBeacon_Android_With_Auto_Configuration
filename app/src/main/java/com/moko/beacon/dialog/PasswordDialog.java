package com.moko.beacon.dialog;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.beacon.R;
import com.moko.beacon.utils.ToastUtils;
import com.moko.support.log.LogModule;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @Date 2017/12/11 0011
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.beacon.dialog.PasswordDialog
 */
public class PasswordDialog extends BaseDialog  {
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.tv_password_ensure)
    TextView tv_password_ensure;

    private String savedPassword;
    private String FILTER_ASCII = "\\A\\p{ASCII}*\\z";

    //private final String FILTER_ASCII = "\\A\\p{ASCII}*\\z"; // J'ai enlevé le final

    //public PasswordDialog(Context context) {
      //  super(context);
    //}

    public PasswordDialog(Context context) {
        super(context);
    }


/*
    private static class SingletonHolder {
        private static final PasswordDialog INSTANCE = new PasswordDialog();
    }

    public static PasswordDialog getInstance() {
        return SingletonHolder.INSTANCE;
    }
*/

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_password;
    }

    @Override
    protected void renderConvertView(View convertView, Object o) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!(source + "").matches(FILTER_ASCII)) {
                    return "";
                }

                return null;
            }
        };
        etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8), filter});
        if (!TextUtils.isEmpty(savedPassword)) {
            etPassword.setText(savedPassword);
            etPassword.setSelection(savedPassword.length());
        }
    }

    @OnClick({R.id.tv_password_cancel, R.id.tv_password_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_password_cancel:
                dismiss();
                passwordClickListener.onDismiss();
                break;
            case R.id.tv_password_ensure:
                dismiss();
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    ToastUtils.showToast(getContext(), getContext().getString(R.string.main_password_null));
                    return;
                }
                if (etPassword.getText().toString().length() != 8) {
                    ToastUtils.showToast(getContext(), getContext().getString(R.string.main_password_length));
                    return;
                }
                passwordClickListener.onEnsureClicked(etPassword.getText().toString());
                break;
        }
    }

    public PasswordClickListener passwordClickListener;

    public void setOnPasswordClicked(PasswordClickListener passwordClickListener) {
        this.passwordClickListener = passwordClickListener;
    }

    public void setSavedPassword(String savedPassword) {
        this.savedPassword = savedPassword;
    }

    public interface PasswordClickListener {

        void onEnsureClicked(String password);

        void onDismiss();
    }

    public void showKeyboard() {
        if (etPassword != null) {
            //Prêt à se concentrer
            etPassword.setFocusable(true);
            etPassword.setFocusableInTouchMode(true);
            //Demander le focus
            etPassword.requestFocus();
            //Appelez la méthode d'entrée du système
            InputMethodManager inputManager = (InputMethodManager) etPassword
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etPassword, 0);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// AUTO CONFIG ////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////


    public void AutoConfirmPass(){
        //TextView TVConfirmPass=(TextView) findViewById(R.id.tv_password_ensure);
        //TVConfirmPass.performClick();
        //tv_password_ensure.setText("OK2");
        //tv_password_ensure.performClick();
    }

    public void AutoSetPassword(String pass) {
        //LogModule.i(etPassword.toString());
        //LogModule.i(String.valueOf(etPassword));
        LogModule.i(String.valueOf(R.id.et_password));
        //EditText test = (EditText) findViewById(R.id.et_password);
        //LogModule.i(String.valueOf(test));
        //test.setText(pass);
        //((EditText) findViewById(R.id.et_password)).setText(myString);
    }

}
