package capstone.abang.com.Register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import capstone.abang.com.Car_Owner.car_owner;
import capstone.abang.com.Models.UDFile;
import capstone.abang.com.Models.UHFile;
import capstone.abang.com.R;
import capstone.abang.com.Utils.Utility;

public class RegisterContinuationActivity extends AppCompatActivity {
    //Declaring all widgets
    private ImageView imgViewNBI;
    private ImageView imgViewSecondaryID;
    private ImageButton imgBtnNbi;
    private ImageButton imgBtnSecondID;
    private RadioGroup radioGroup;
    private RadioButton radioButtonOwner;
    private RadioButton radioButtonRenter;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    //Declaring all holders
    private String name = null;
    private String pass = null;
    private String email = null;
    private String username = null;
    private String addr = null;
    private String type = null;
    private String contact = null;

    //Responsible for photos
    private String userChoosenTask;
    private Uri holder1 = null;
    private Uri holder2 = null;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private int holder = 0;

    //Firebase things
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatabaseUserHeader;
    private DatabaseReference mDatabaseUserDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiser_continuation);
        //Referencing all widgets
        imgViewNBI = findViewById(R.id.imgViewNbi);
        imgViewSecondaryID = findViewById(R.id.imgViewSecondId);
        imgBtnNbi = findViewById(R.id.btnNBI);
        imgBtnSecondID = findViewById(R.id.btnSecondaryID);
        radioGroup = findViewById(R.id.userType);
        radioButtonOwner = findViewById(R.id.radioOwner);
        radioButtonRenter = findViewById(R.id.radioRenter);
        btnRegister = findViewById(R.id.btnRegContinuation);
        progressDialog = new ProgressDialog(this);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Account");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Firebase things
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUserHeader = FirebaseDatabase.getInstance().getReference("UHFile");
        mDatabaseUserDetail = FirebaseDatabase.getInstance().getReference("UDFile");
        mStorage = FirebaseStorage.getInstance().getReference("Photos");

        //Methods
        setInit();
        setImage();
    }

    private void setImage() {
        imgBtnNbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                holder = 1;
            }
        });
        imgBtnSecondID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                holder = 2;
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterContinuationActivity.this);
        builder.setTitle("Choose picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(RegisterContinuationActivity.this);

                if(items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if(result) {
                        cameraIntent();
                    }
                }
                else if(items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if(result) {
                        galleryIntent();
                    }
                } else if(items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if(requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            imgViewNBI.setImageURI(uri);
            imgViewNBI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1 = uri;
            imgViewNBI.setBackgroundResource(0);
        }
        else {
            imgViewSecondaryID.setImageURI(uri);
            imgViewSecondaryID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder2 = uri;
            imgViewSecondaryID.setBackgroundResource(0);
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if(holder == 1) {
            imgViewNBI.setImageURI(uri);
            imgViewNBI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder1 = uri;
            imgViewNBI.setBackgroundResource(0);
        }
        else {
            imgViewSecondaryID.setImageURI(uri);
            imgViewSecondaryID.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder2 = uri;
            imgViewSecondaryID.setBackgroundResource(0);
        }
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable!=null);

        if(hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    private void setInit() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasImage(imgViewNBI) || !hasImage(imgViewSecondaryID) || radioGroup.getCheckedRadioButtonId() == -1) {
                    toastMethod("Fill necessary info");
                }
                else {
                    registerAccount();
                }
            }
        });
    }

    private void registerAccount() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            name = bundle.getString("name");
            pass = bundle.getString("password");
            email = bundle.getString("email");
            username = bundle.getString("username");
            addr = bundle.getString("addr");
            contact = bundle.getString("contact");
            if(radioButtonRenter.isChecked()) {
                type = "Renter";
            } else if(radioButtonOwner.isChecked()) {
                type = "Owner";
            }
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String id = user.getUid();
                            //Insert data to UHFile
                            UHFile newUserHeader = new UHFile(id, username, pass, "AC");
                            mDatabaseUserHeader.child(id).setValue(newUserHeader);
                            final StorageReference nbiFile = mStorage.child(id).child(holder1.getLastPathSegment());
                            nbiFile.putFile(holder1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Uri nbiUri = taskSnapshot.getDownloadUrl();
                                    String strNBI = nbiUri.toString();
                                    //Insert data to UDFile
                                    UDFile newUserDetail = new UDFile(id, name, addr, email, "AC", type, contact, strNBI);
                                    mDatabaseUserDetail.child(id).setValue(newUserDetail);
                                    progressDialog.hide();
                                    toastMethod("Successfully Registered");
                                    if(type.equals("Owner")) {
                                        Intent intent = new Intent(getApplicationContext(), car_owner.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else {
                            toastMethod("Technical difficulties");
                            progressDialog.hide();
                        }
                    }
                });
    }

    private void toastMethod(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //PERMISSION FOR CAMERA AND GALLERY
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo")) {
                        cameraIntent();
                    } else if(userChoosenTask.equals("Choose from Gallery")) {
                        galleryIntent();
                    }
                } else {
                    toastMethod("Denied!");
                }
                break;
        }
    }
}