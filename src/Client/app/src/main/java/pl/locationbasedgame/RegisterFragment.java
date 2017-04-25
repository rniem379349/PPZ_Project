package pl.locationbasedgame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO: 15-Apr-17 RESOLVE CODE CODE DUPLICATION SOMEHOW
public class RegisterFragment extends Fragment implements RegistrationResultListener {

    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText repeatedPasswordEditText;
    private EditText mailEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEditTexts();
        assignListeners();
    }

    private void getEditTexts() {
        repeatedPasswordEditText = (EditText) getView().findViewById(R.id.et_repeated_password);
        nameEditText = (EditText) getView().findViewById(R.id.et_name);
        mailEditText = (EditText) getView().findViewById(R.id.et_mail);
        passwordEditText = (EditText) getView().findViewById(R.id.et_password);
    }

    private void assignListeners() {
        Button registerButton = (Button) getView().findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String repeatedPassword = repeatedPasswordEditText.getText().toString();
        String mail = mailEditText.getText().toString();

        if (areSpecified(name, password, repeatedPassword, mail)) {
            if (areBothPasswordsNotEqual(password, repeatedPassword)) {
                Toast.makeText(getContext(), R.string.passwords_dont_match, Toast.LENGTH_SHORT).show();
            } else {
                StartActivity.getService().sendRegisterRequestToServer(name, password, mail, this);
            }
        } else {
            Toast.makeText(getContext(), R.string.fill_form, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areSpecified(String name, String password, String repeated, String mail) {
        return !name.isEmpty() && !password.isEmpty() && !repeated.isEmpty() && !mail.isEmpty();
    }

    private boolean areBothPasswordsNotEqual(String password, String repeatedPassword) {
        return !password.equals(repeatedPassword);
    }

    @Override
    public void onRegistrationSuccess() {
        Toast.makeText(getContext(), R.string.registration_success, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegistrationFailure(String error) {
        Toast.makeText(getContext(), R.string.registration_error, Toast.LENGTH_LONG).show();
        Log.i("ERROR", error);
    }
}