package com.manyong.membermanagement.login;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manyong.membermanagement.R;
import com.manyong.membermanagement.database.dbHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hanman-yong on 2020-01-07.
 */
public class JoinActivity extends AppCompatActivity {

    dbHandler handler;

    EditText id, password, password2, name, phone, birthday, admin_code;
    ImageView setImage, unsetImage;
    Button join, id_check, cancel;
    RadioButton normal_radio, admin_radio;
    String admin_code_str = "admin11";
    static int count = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        if (handler == null) {
            handler = dbHandler.open(this);
        }

        join = (Button) findViewById(R.id.last_join);
        id_check = (Button) findViewById(R.id.join_id_chack);
        cancel = (Button) findViewById(R.id.last_cancel);

        id = (EditText) findViewById(R.id.join_id);
        password = (EditText) findViewById(R.id.join_password);
        password2 = (EditText) findViewById(R.id.join_password2);
        name = (EditText) findViewById(R.id.join_name);
        birthday = (EditText) findViewById(R.id.join_birthday);
        phone = (EditText) findViewById(R.id.join_phone);
        admin_code = (EditText) findViewById(R.id.admin_code);

        normal_radio = (RadioButton) findViewById(R.id.normal_member);
        admin_radio = (RadioButton) findViewById(R.id.admin_member);

        setImage = (ImageView) findViewById(R.id.Image_chack);
        unsetImage = (ImageView) findViewById(R.id.Image_chack);

        id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(id);
                count++;
            }
        });

        //페스워드 일치 확인
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().equals(password2.getText().toString())) {
                    setImage.setImageResource(R.drawable.chack);
                } else
                    unsetImage.setImageResource(R.drawable.unchack);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        admin_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(admin_code.getText().toString().equals(admin_code_str)){
                    admin_radio.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        normal_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admin_radio.setChecked(false);
            }
        });

        admin_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normal_radio.setChecked(false);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(join);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 취소 후 로그인 페이지로 이동
                finish();
            }
        });

    }

    public void search(EditText edit_id_check) {

        String id_check = edit_id_check.getText().toString();
        Cursor cursor;
        boolean flag = true;

        cursor = handler.member_select_one(id_check);

        while(cursor.moveToNext()) {
            if (cursor != null) {
                Toast.makeText(getApplicationContext(), "아이디 중복", Toast.LENGTH_SHORT).show();
                edit_id_check.setText("");
                flag = false;
            }
        }

        cursor.close();

        if (flag) {
            Toast.makeText(getApplicationContext(), "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void insert(View target) {
        String insert_id = id.getText().toString();
        String insert_password = password.getText().toString();
        String insert_password2 = password2.getText().toString();
        String insert_name = name.getText().toString();
        String insert_phone = phone.getText().toString();
        String insert_birthday = birthday.getText().toString();
        String insert_classes = "";

        // 회원가입 양식에 빈칸이 있는 경우
        if (insert_id.length() == 0) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_password.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_password2.length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호 확인란을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_name.length() == 0) {
            Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_phone.length() == 0) {
            Toast.makeText(getApplicationContext(), "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        } else if (insert_birthday.length() == 0) {
            Toast.makeText(getApplicationContext(), "생년월일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(normal_radio.isChecked()){
            insert_classes = "일반회원";
        } else {
            insert_classes = "관리자";
        }

        // 회원 가입 날짜 불러오기
        Date currentTime = Calendar.getInstance().getTime();
        String insert_reg_date = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(currentTime);

        if (this.count != 1) {
            Toast.makeText(getApplicationContext(), "아이디 중복 확인 해주세요.", Toast.LENGTH_SHORT).show();
            this.count = 0;

        } else if (insert_password.equals(insert_password2) == false && this.count >= 1) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다. 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();

            password.setText("");
            password2.setText("");

            this.count = 0;
            Toast.makeText(getApplicationContext(), count, Toast.LENGTH_SHORT).show();

        } else if (insert_password.equals(insert_password2) && this.count >= 1) {

            handler.member_insert(insert_id, insert_password, insert_name, insert_birthday, insert_phone, insert_classes, insert_reg_date);

            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();

            this.count = 0;

            // 회원가입이 성공하면 로그인 화면으로 이동
//            Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
//            startActivity(intent);
            finish();
        }
    }

}
