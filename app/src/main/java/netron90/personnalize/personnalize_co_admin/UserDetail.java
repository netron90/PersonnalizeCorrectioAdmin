package netron90.personnalize.personnalize_co_admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserDetail extends AppCompatActivity {

    private TextView userName, userEmail, userPhone;
    public static final String USER_NAME_KEY = "userNameKey", USER_EMAIL_KEY = "userEmailKey",
    USER_PHONE_KEY = "userPhoneKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        userName  = (TextView) findViewById(R.id.user_name_tv);
        userEmail = (TextView) findViewById(R.id.user_email_tv);
        userPhone = (TextView) findViewById(R.id.user_phone_tv);

        String userNameIntent = getIntent().getStringExtra(USER_NAME_KEY);
        String userEmailIntent = getIntent().getStringExtra(USER_EMAIL_KEY);
        String userPhoneIntent = getIntent().getStringExtra(USER_PHONE_KEY);

        userName.setText(userNameIntent);
        userEmail.setText(userEmailIntent);
        userPhone.setText(userPhoneIntent);
    }
}
