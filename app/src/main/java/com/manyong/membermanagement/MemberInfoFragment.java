package com.manyong.membermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.manyong.membermanagement.database.dbHandler;
import com.manyong.membermanagement.event.ActivityResultEvent;
import com.manyong.membermanagement.login.LoginActivity;
import com.manyong.membermanagement.util.BusProvider;
import com.manyong.membermanagement.util.LoginSharedPreference;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;

public class MemberInfoFragment extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    private final String TAG = "Member Info";

    private EditText id, name, password, phone, birthday, pw, pw_check;
    private ImageView profile_image;
    private Button btn_update, btn_pw_change;
    private String mCurrentPhotoPath, my_id, id_str, pw_str;

    private File tempFile;

    private dbHandler handler;

    private Cursor member_cursor = null;
    private LayoutInflater inflater;

    public MemberInfoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_member_info, container, false);

        if (handler == null) {
            handler = dbHandler.open(getContext());
        }

        my_id = LoginSharedPreference.getAttribute(getContext(), LoginActivity.LOGIN_ID);
        Log.d(TAG, my_id);

        id = (EditText) view.findViewById(R.id.my_page_id);
        name = (EditText) view.findViewById(R.id.my_page_name);
        password = (EditText) view.findViewById(R.id.my_page_password);
        phone = (EditText) view.findViewById(R.id.my_page_phone);
        birthday = (EditText) view.findViewById(R.id.my_page_birthday);

        profile_image = (ImageView) view.findViewById(R.id.my_page_profile_image);

        btn_update = (Button) view.findViewById(R.id.my_page_update);
        btn_pw_change = (Button) view.findViewById(R.id.my_page_pw_change);

        MyInfo();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setTitle("실행할 메뉴를 선택하세요.");

                final ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice);
                adapter.add("카메라");
                adapter.add("앨범");

                alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id) {
                            case 0:
                                getImageFromCamera();
                                break;
                            case 1:
                                getImageFromAlbum();
                                break;
                        }
                    }
                });

                alertBuilder.show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                onUpdate();

                MyInfo();

                password.setText("");
            }
        });

        btn_pw_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                onPWChange();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();

    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onActivityResultEvent(@NonNull ActivityResultEvent event) {
        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "프래그먼트도 실행되고있다구!!");
        if (resultCode != getActivity().RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                if (data.getData() != null) {
                    Uri photoUri = data.getData();
                    Cursor cursor = null;

                    try {
                        String[] proj = {MediaStore.Images.Media.DATA};

                        assert photoUri != null;
                        cursor = getContext().getContentResolver().query(photoUri, proj, null, null, null);

                        assert cursor != null;
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        cursor.moveToFirst();

                        tempFile = new File(cursor.getString(column_index));

                        mCurrentPhotoPath = tempFile.getAbsolutePath();

                        galleryAddPic();

                        onUriUpdate();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "앨범에서 가져오기 에러 : " + e.getMessage());

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
            }
            case PICK_FROM_CAMERA: {
                try {
                    Log.v(TAG, "PICK_FROM_CAMERA 처리");
                    galleryAddPic();
                    onUriUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void MyInfo() {

        member_cursor = handler.member_select_one(my_id);
        member_cursor.moveToFirst();

        id.setText(member_cursor.getString(member_cursor.getColumnIndex("id")));
        name.setText(member_cursor.getString(member_cursor.getColumnIndex("name")));

        phone.setText(member_cursor.getString(member_cursor.getColumnIndex("phone")));
        birthday.setText(member_cursor.getString(member_cursor.getColumnIndex("birthday")));

        try {
            Uri uri = Uri.parse(member_cursor.getString(member_cursor.getColumnIndex("profile_image")));
            if(uri != null) {
                profile_image.setImageURI(uri);
            } else {
                profile_image.setImageResource(R.drawable.user);
            }
        } catch (Exception e){
            Log.e(TAG, "이미지 없거나 오류임.." + e.getMessage());
        }

        member_cursor.close();
    }


    public File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".png";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "manyong");

        if (!storageDir.exists()) {
            Log.v(TAG, "storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }

        Log.v(TAG, "storageDir 존재함 " + storageDir.toString());

        imageFile = new File(storageDir, imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    public void galleryAddPic() throws Exception {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Log.v(TAG, mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
        Toast.makeText(getContext(), "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
    }

    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), photoFile);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    getActivity().startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        } else {
            Log.v(TAG, "저장공간에 접근 불가능");
            return;
        }
    }

    public void onUriUpdate() {
        handler.member_profile_update(my_id, mCurrentPhotoPath);
        Uri image = Uri.parse(mCurrentPhotoPath);
        profile_image.setImageURI(image);
        mCurrentPhotoPath = null;
    }

    private void onUpdate() {
        id_str = id.getText().toString();
        pw_str = password.getText().toString();

        String name_str = name.getText().toString();
        String phone_str = phone.getText().toString();
        String birthday_str = birthday.getText().toString();

        member_cursor = handler.member_select_one(id_str);

        String ck_pw = member_cursor.getString(member_cursor.getColumnIndex("password"));

        if (pw_str.equals(ck_pw) == false) {
            Toast.makeText(getActivity(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            password.setText("");
            return;
        }

        handler.member_update(id_str, name_str, phone_str, birthday_str);

        Toast.makeText(getActivity(), "회원정보 수정 완료.", Toast.LENGTH_SHORT).show();

        password.setText("");

        member_cursor.close();

    }

    // 비밀번호 변경을 위한 다이얼로그 생성
    public void onPWChange() {
        id_str = id.getText().toString();
        pw_str = password.getText().toString();

        member_cursor = handler.member_select_one(id_str);

        String ck_pw = member_cursor.getString(member_cursor.getColumnIndex("password"));

        if (pw_str.equals(ck_pw) == false) {
            Toast.makeText(getActivity(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            password.setText("");
            return;
        }

        // 레이아웃 XML파일을 View객체로 만들기 위해 inflater 사용
        View view = inflater.inflate(R.layout.fragment_pwd_change, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        pw = (EditText) view.findViewById(R.id.my_page_pw);
        pw_check = (EditText) view.findViewById(R.id.my_page_pw_check);

        builder.setTitle("변경할 비밀번호를 입력해 주세요.")
                .setView(view)
                .setPositiveButton("변경하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String str_pw = pw.getText().toString();
                        String str_pw_check = pw_check.getText().toString();

                        if (str_pw.equals(str_pw_check)) {
                            handler.member_pw_update(id_str, str_pw);
                            Toast.makeText(getActivity(), "비밀번호 수정 완료.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다. 다시 시도해주세요. ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            onPWChange();
                        }

                        password.setText("");

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "취소.", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }).show();

        member_cursor.close();
    }
}