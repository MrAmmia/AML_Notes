package net.thebookofcode.www.amlnotes;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.thebookofcode.www.amlnotes.Adapters.TodoAdapter;
import net.thebookofcode.www.amlnotes.Entities.Note;
import net.thebookofcode.www.amlnotes.Model.NoteViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditNoteFragment extends Fragment {

    private static final int REQUEST_CODE_READ_PERMISSION = 1;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 2;

    private NotificationManagerCompat notificationManagerCompat;

    String imageUrl = "";
    String todo = "";
    String doneString = "";
    String reminder_date_time = "";
    private ArrayList<String> todosArray = new ArrayList<>();
    private ArrayList<Integer> boolArray = new ArrayList<>();
    private NoteViewModel noteViewModel;
    Boolean isEditing = true;

    Note note;

    ImageView imgBack;
    ImageView imgDone;
    LinearLayout llNote;
    EditText editTextTitle;
    RelativeLayout layoutImage;
    ImageView imgNote;
    ImageView imgDelete;
    LinearLayout layoutTodo;
    LinearLayout addTodo;
    EditText editTextTodo;
    EditText editTextContent;
    LinearLayout linearLayout;
    LinearLayout llAlbum;
    LinearLayout llTodo;
    LinearLayout llRemind;
    RecyclerView todoList;
    TextView tvDateTime;

    TodoAdapter adapter;

    public AddEditNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit_note, container, false);

        imgBack = view.findViewById(R.id.imgBack);
        imgDone = view.findViewById(R.id.imgDone);
        llNote = view.findViewById(R.id.llNote);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        layoutImage = view.findViewById(R.id.layoutImage);
        imgNote = view.findViewById(R.id.imgNote);
        imgDelete = view.findViewById(R.id.imgDelete);
        layoutTodo = view.findViewById(R.id.layoutTodo);
        linearLayout = view.findViewById(R.id.linearLayout);
        editTextTodo = view.findViewById(R.id.editTextTodo);
        editTextContent = view.findViewById(R.id.editTextContent);
        llAlbum = view.findViewById(R.id.llAlbum);
        llTodo = view.findViewById(R.id.llTodo);
        llRemind = view.findViewById(R.id.llRemind);
        todoList = view.findViewById(R.id.todoList);
        tvDateTime = view.findViewById(R.id.tvDateTime);
        addTodo = view.findViewById(R.id.addTodo);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteViewModel = new ViewModelProvider(getActivity()).get(NoteViewModel.class);
        todoList.setLayoutManager(new LinearLayoutManager(getContext()));
        editTextTodo.setImeOptions(EditorInfo.IME_ACTION_DONE);
        todoList.setHasFixedSize(true);
        adapter = new TodoAdapter();
        todoList.setAdapter(adapter);
        if (getArguments() != null) {
            AddEditNoteFragmentArgs args = AddEditNoteFragmentArgs.fromBundle(getArguments());
            note = args.getCurrentNote();
            if (note != null) {
                isEditing = false;
                imgDone.setBackgroundResource(R.drawable.ic_delete);
                editTextTitle.setText(note.getTitle());
                tvDateTime.setText(note.getDateTime());
                editTextContent.setText(note.getContent());
                if (!note.getImgPath().isEmpty()) {
                    imageUrl = note.getImgPath();
                    visibleImage(getImageFromPath(imageUrl));
                }
                if (!note.getTodo().isEmpty()) {
                    todo = note.getTodo();
                    doneString = note.getDoneString();
                    String[] todos = todo.split("\n");
                    String[] bools = doneString.split("\n");
                    todosArray = new ArrayList<>();
                    boolArray = new ArrayList<>();
                    for (int i = 0; i < todos.length; i++) {
                        todosArray.add(todos[i]);
                        boolArray.add(Integer.valueOf(bools[i]));
                    }
                    adapter.setTodosArray(todosArray);
                    adapter.setDoneArray(boolArray);
                    layoutTodo.setVisibility(View.VISIBLE);
                    todoList.setVisibility(View.VISIBLE);
                }
            } else {
                // Toast.makeText(getContext(), "New Note", Toast.LENGTH_SHORT).show();
            }
        }

        if (isEditing) {
            imgDone.setBackgroundResource(R.drawable.ic_check);
        } else {
            imgDone.setBackgroundResource(R.drawable.ic_delete);
        }

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextContent.hasFocus();
            }
        });

        imgDone.setOnClickListener(v -> {
            if (isEditing) {
                saveNote();
                isEditing = false;
                imgDone.setBackgroundResource(R.drawable.ic_delete);
            } else {
                // delete note
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deleteNote(note);
                    }
                }).start();
                getActivity().onBackPressed();
            }
        });

        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextTodo.setVisibility(View.VISIBLE);
                editTextTodo.hasFocus();
            }
        });

        imgBack.setOnClickListener(v -> {
            saveNote();
            getActivity().onBackPressed();
        });

        llAlbum.setOnClickListener(v -> {
            showBottomSheetDialog(getContext());

        });

        llRemind.setEnabled(true);

        llRemind.setOnClickListener(v -> {
            // Set Reminder
            showAlarmPicker(getContext());
        });

        llNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextContent.requestFocus();
            }
        });

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideImage();
            }
        });

        llTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTodo.setVisibility(View.VISIBLE);
                editTextTodo.setVisibility(View.VISIBLE);
            }
        });

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEditing = true;
                imgDone.setBackgroundResource(R.drawable.ic_check);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEditing = true;
                imgDone.setBackgroundResource(R.drawable.ic_check);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextTodo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6 && !v.getText().toString().isEmpty()) {
                    adapter.setTodoString(v.getText().toString());
                    adapter.setTodoBool(0);
                    todoList.setVisibility(View.VISIBLE);
                    isEditing = true;
                    imgDone.setBackgroundResource(R.drawable.ic_check);
                    v.setText("");
                    return true;
                } else if (actionId == KeyEvent.KEYCODE_DEL || actionId == KeyEvent.KEYCODE_FORWARD_DEL && v.getText().toString().isEmpty()) {
                    editTextTodo.setVisibility(View.GONE);
                    return true;
                }

                return false;
            }
        });
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String body = editTextContent.getText().toString();

        String dateTime = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault()).format(new Date());
        int numDone = 0;
        int numTotal = 0;

        if (title.trim().isEmpty() && body.trim().isEmpty() && imgNote.getVisibility() == View.GONE
                && layoutTodo.getVisibility() == View.GONE && reminder_date_time.trim().isEmpty()) {
            //requireActivity().onBackPressed();
        } else {
            if (title.trim().isEmpty() && body.trim().isEmpty()) {
                if (imgNote.getVisibility() == View.VISIBLE) {
                    title = "Picture List";
                } else if (layoutTodo.getVisibility() == View.VISIBLE) {
                    if (!adapter.getTodoString().isEmpty()) {
                        title = "Todo List";
                        doneString = adapter.getDoneString();
                        todo = adapter.getTodoString();
                        numDone = adapter.numDone();
                        numTotal = adapter.getItemCount();
                    }

                }
            }
            if (note != null) {
                int id = note.getId();
                note = new Note(title, body, dateTime, imageUrl, todo, doneString, numDone, numTotal, reminder_date_time);
                note.setId(id);
                noteViewModel.update(note);
            } else {
                note = new Note(title, body, dateTime, imageUrl, todo, doneString, numDone, numTotal, reminder_date_time);
                noteViewModel.insert(note);
            }
        }
        isEditing = false;
        imgDone.setBackgroundResource(R.drawable.ic_delete);
    }

    private void deleteNote(Note note) {
        noteViewModel.delete(note);
    }

    private void showBottomSheetDialog(Context context) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        LinearLayout camera = bottomSheetDialog.findViewById(R.id.cameraLayout);
        LinearLayout gallery = bottomSheetDialog.findViewById(R.id.galleryLayout);


        camera.setOnClickListener(v ->
        {
            PackageManager packageManager = getActivity().getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (Build.VERSION.SDK_INT <= 28) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        bottomSheetDialog.cancel();
                        takePicture();
                        galleryAddPic(getContext(), imageUrl);
                        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                        visibleImage(bitmap);
                        isEditing = true;
                        imgDone.setBackgroundResource(R.drawable.ic_check);
                    } else {
                        askPermission(REQUEST_CODE_WRITE_PERMISSION);
                    }
                } else {
                    bottomSheetDialog.cancel();
                    takePicture();
                    galleryAddPic(getContext(), imageUrl);
                    Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                    visibleImage(bitmap);
                    isEditing = true;
                    imgDone.setBackgroundResource(R.drawable.ic_check);
                }
            }
        });

        gallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                bottomSheetDialog.cancel();
                selectImage();
                isEditing = true;
                imgDone.setBackgroundResource(R.drawable.ic_check);
            } else {
                askPermission(REQUEST_CODE_READ_PERMISSION);
            }

        });

        bottomSheetDialog.show();
    }

    private void startAlarm(Context context, Calendar c) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);
    }

    private void showAlarmPicker(Context context) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.alarm_picker_dialog);
        TextView dateTimeSelectedTextView = bottomSheetDialog.findViewById(R.id.dateTimeSelectedTextView);
        DatePicker datePicker = bottomSheetDialog.findViewById(R.id.datePicker);
        TimePicker timePicker = bottomSheetDialog.findViewById(R.id.timePicker);
        LinearLayout llCancel = bottomSheetDialog.findViewById(R.id.llCancel);
        LinearLayout llConfirm = bottomSheetDialog.findViewById(R.id.llConfirm);
        final Calendar c = Calendar.getInstance();
        datePicker.setMinDate(c.getTimeInMillis());
        String setDateTime = new SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault()).format((c.getTime()).getTime());
        dateTimeSelectedTextView.setText(setDateTime);

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String setDateTime = new SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault()).format((c.getTime()).getTime());
                dateTimeSelectedTextView.setText(setDateTime);
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                String setDateTime = new SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault()).format((c.getTime()).getTime());
                dateTimeSelectedTextView.setText(setDateTime);
            }
        });

        llConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarm(context, c);
                isEditing = true;
                imgDone.setBackgroundResource(R.drawable.ic_check);
                bottomSheetDialog.cancel();
                reminder_date_time = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault()).format(c.getTime());
            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                reminder_date_time = "";
            }
        });
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.show();
    }

    private void selectImage() {
        Intent showGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (showGalleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            galleryLauncher.launch(showGalleryIntent);
            //startActivityForResult(showGalleryIntent,REQUEST_SELECT_IMAGE);
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "net.thebookofcode.www.amlnotes.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private void galleryAddPic(Context context, String url) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(url);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        showBottomSheetDialog(getContext());
                    } else {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Do your code from onActivityResult
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageurl = data.getData();
                            if (selectedImageurl != null) {
                                imageUrl = getImageUrl(selectedImageurl);
                                try {
                                    InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageurl);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    visibleImage(bitmap);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                imageUrl = "";
                            }
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Do your code from onActivityResult
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        galleryAddPic(getContext(), imageUrl);
                        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                        visibleImage(bitmap);
                        /*if (data != null) {
                            Uri extras = data.getData();
                            try {
                                visibleImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), extras));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            galleryAddPic(getContext(), imageUrl);
                            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                            visibleImage(bitmap);
                        }*/
                    }
                }
            });

    private String getImageUrl(Uri uri) {
        String filePath;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null);
        if (cursor == null) {
            filePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imageUrl = image.getAbsolutePath();
        return image;
    }

    private void visibleImage(Bitmap bitmap) {
        layoutImage.setVisibility(View.VISIBLE);
        imgNote.setVisibility(View.VISIBLE);
        imgDelete.setVisibility(View.VISIBLE);
        imgNote.setImageBitmap(bitmap);
    }

    private void hideImage() {
        imgNote.setVisibility(View.GONE);
        imgDelete.setVisibility(View.GONE);
        layoutImage.setVisibility(View.GONE);
        imgNote.setImageDrawable(null);
        deleteImage(imageUrl);
        imageUrl = "";
        note.setImgPath("");
        isEditing = true;
        imgDone.setBackgroundResource(R.drawable.ic_check);
    }

    private void deleteImage(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            imgFile.delete();
        }
    }

    private Bitmap getImageFromPath(String path) {
        Bitmap myBitmap = null;
        File imgFile = new File(path);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

    private void askPermission(int requestCode) {
        if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_PERMISSION);
        } else if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
        }

    }
}