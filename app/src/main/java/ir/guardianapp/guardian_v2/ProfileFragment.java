package ir.guardianapp.guardian_v2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.guardianapp.guardian_v2.database.ImageSavingManager;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class ProfileFragment extends Fragment {

    static final int DEFAULT_THREAD_POOL_SIZE = 5;
    static ExecutorService executorService;
    private Handler handler;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText phoneNumEditText;

    private static int RESULT_LOAD_IMAGE = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        phoneNumEditText = view.findViewById(R.id.phoneNumEditText);
        Button saveButton = view.findViewById(R.id.saveProfileButton);
        ProgressBar editProgress = view.findViewById(R.id.progressBar);

        User user = User.getInstance();
        final String[] userText = {user.getUsername(), user.getPassword(), user.getPhoneNum()};
        saveButton.setOnClickListener(v -> {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
            if(usernameEditText.getText().length() == 0) {
//                Toast.makeText(getContext(), "نام کاربر نمی تواند خالی باشد.", Toast.LENGTH_LONG).show();

            } else if(passwordEditText.getText().length() == 0) {
//                Toast.makeText(getContext(), "رمز عبور نمی تواند خالی باشد.", Toast.LENGTH_LONG).show();
            } if((phoneNumEditText.getText().length() != 11) && (phoneNumEditText.getText().length() != 0)) {
                Toast.makeText(getContext(), "شماره همراه باید ۱۱ رقم باشد!", Toast.LENGTH_LONG).show();
            } else if(!phoneNumEditText.getText().toString().startsWith("09") && (phoneNumEditText.getText().length() != 0)) {
                Toast.makeText(getContext(), "شماره همراه وارد شده صحیح نمی باشد.", Toast.LENGTH_LONG).show();
            }
            //
            if(!userText[0].equals(usernameEditText.getText().toString())
            || !userText[1].equals(passwordEditText.getText().toString())
            || !userText[2].equals(phoneNumEditText.getText().toString())) {
                executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MessageResult.SUCCESSFUL) {
                            editProgress.setVisibility(View.INVISIBLE);
                            editProgress.setProgress(editProgress.getProgress());
                            Toast.makeText(getContext(), "ویرایش با موفقیت انجام شد.", Toast.LENGTH_LONG).show();
                            setProfile(usernameEditText, passwordEditText, phoneNumEditText);

                        } else if(msg.what == MessageResult.USERNAME_IS_NOT_UNIQUE) {
                            editProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "نام کاربری وارد شده تکراری می باشد.", Toast.LENGTH_LONG).show();

                        } else if(msg.what == MessageResult.PHONE_IS_NOT_UNIQUE) {
                            editProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "شماره همراه وارد شده تکراری می باشد.", Toast.LENGTH_LONG).show();

                        } else if(msg.what == MessageResult.FAILED) {
                            editProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "لطفا از حساب کاربری خود خارج شوید و دوباره ورود کنید.", Toast.LENGTH_LONG).show();

                        } else {
                            editProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_LONG).show();

                        }
                    }
                };
                if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
                    editProgress.setVisibility(View.VISIBLE);
                    if(usernameEditText.getText().length() > 0) {
                        userText[0] = usernameEditText.getText().toString();
                    }
                    if(passwordEditText.getText().length() > 0) {
                        userText[1] = passwordEditText.getText().toString();
                    }
                    if(phoneNumEditText.getText().length() > 0) {
                        userText[2] = phoneNumEditText.getText().toString();
                    }
                    executorService.submit(ThreadGenerator.editProfile(user.getUsername(), user.getPassword(), user.getPhoneNum(), user.getToken(),
                            userText[0], userText[1], userText[2], handler));
                } else {
                    Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MessageResult.SUCCESSFUL) {
                        editProgress.setVisibility(View.INVISIBLE);
                        editProgress.setProgress(editProgress.getProgress());
                        user.setToken("");
                        user.setUsername("");
                        user.setPassword("");
                        user.setPhoneNum("");
                        Toast.makeText(getContext(), "لطفا چند لحظه منتظر بمانید.", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    } else if(msg.what == MessageResult.FAILED) {
                        editProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "لطفا از حساب کاربری خود خارج شوید و دوباره ورود کنید.", Toast.LENGTH_LONG).show();

                    } else {
                        editProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_LONG).show();
                    }
                }
            };

            if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
                editProgress.setVisibility(View.VISIBLE);
                executorService.submit(ThreadGenerator.logoutUser(User.getInstance().getUsername(), User.getInstance().getToken(), handler));
            } else {
                Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_LONG).show();
            }
        });

        Button profilePhotoSelectButton = view.findViewById(R.id.profilePhotoSelectButton);
        profilePhotoSelectButton.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfile(usernameEditText, passwordEditText, phoneNumEditText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            de.hdodenhof.circleimageview.CircleImageView imageView = getView().findViewById(R.id.imageView);
            try (ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(selectedImage, "r")) {
                if (pfd != null) {
                    imageView.setImageBitmap(BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor()));
                    ImageSavingManager.saveToInternalStorage(BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor()), getContext());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setProfile(EditText username, EditText password, EditText phone) {
        User user = User.getInstance();
        username.setHint(user.getUsername());
        password.setHint(user.getPassword());
        phone.setHint(user.getPhoneNum());
        ImageSavingManager.loadImageFromStorage(getView().findViewById(R.id.imageView), getContext());
    }
}