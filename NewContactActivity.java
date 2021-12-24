package com.example.contact;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import static android.os.Environment.getExternalStoragePublicDirectory;

public class NewContactActivity extends AppCompatActivity {
    private TextInputLayout edtName, edtPhone, edtEmail, edtAddress;
    private ImageButton btnBack;
    private TextView tvAdd, tvToolbarTitle;
//    private CircleImageView ic_create_img;
    String id, Name, Phone, Email, Address, message;
    String pathToFile;
    byte[] photo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        //initializing vars
        String []data;
        edtName = (TextInputLayout) findViewById(R.id.edtName);
        edtPhone = (TextInputLayout) findViewById(R.id.edtPhone);
        edtEmail = (TextInputLayout) findViewById(R.id.edtEmail);
        edtAddress = (TextInputLayout) findViewById(R.id.edtAddress);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
//        ic_create_img = (CircleImageView) findViewById(R.id.profile_image);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        //Nhận dữ liệu intent được gửi xem rằng là sửa hay là thêm
        message = getIntent().getExtras().getString("MESSAGE");
        if(message.equals("ADD_CONTACT")){
            tvToolbarTitle.setText("Thêm danh bạ");
        }else if(message.equals("ADD_CONTACT_QRCODE")){
            tvToolbarTitle.setText("Thêm danh bạ");
            Toast.makeText(NewContactActivity.this, "Quét thành công danh bạ"
            , Toast.LENGTH_SHORT).show();
            data = getIntent().getExtras().getString("DATA_QRCODE").split("\\,");
            edtName.getEditText().setText(data[0]);
            if(!data[1].equals("null")){
                edtPhone.getEditText().setText(data[1]);
            }
            if(!data[2].equals("null")){
                edtEmail.getEditText().setText(data[2]);
            }
            if(!data[3].equals("null")){
                edtAddress.getEditText().setText(data[3]);
            }
        }else if(message.equals("CHANGE_CONTACT")) {
            tvToolbarTitle.setText("Sửa danh bạ");
            id = getIntent().getExtras().getString("ID");
            Name = getIntent().getExtras().getString("NAME");
            Phone = getIntent().getExtras().getString("PHONE");
            Email = getIntent().getExtras().getString("EMAIL");
            Address = getIntent().getExtras().getString("ADDRESS");

            edtName.getEditText().setText(Name);
            edtPhone.getEditText().setText(Phone);
            edtEmail.getEditText().setText(Email);
            edtAddress.getEditText().setText(Address);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
//        ic_create_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takeActionPhoto();
//            }
//        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            edtName = (TextInputLayout) findViewById(R.id.edtName);
            edtPhone = (TextInputLayout) findViewById(R.id.edtPhone);
            edtEmail = (TextInputLayout) findViewById(R.id.edtEmail);
            edtAddress = (TextInputLayout) findViewById(R.id.edtAddress);

            String name = edtName.getEditText().getText().toString();
            String phone = edtPhone.getEditText().getText().toString();
            String email = edtEmail.getEditText().getText().toString();
            String address = edtAddress.getEditText().getText().toString();

            Intent intent = new Intent(NewContactActivity.this, MainActivity.class);
            Intent intentChange = new Intent(NewContactActivity.this, ContactDetailActivity.class);
                if(message.equals("ADD_CONTACT") || message.equals("ADD_CONTACT_QRCODE")){
                ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());
                if(edtName.getEditText().getText().toString().equals("") && edtPhone.getEditText().getText().toString().equals("")
                        && edtEmail.getEditText().getText().toString().equals("") && edtAddress.getEditText().getText().toString().equals("")){
                    finish();
                    Toast.makeText(getApplicationContext(), "Không có gì để lưu đã hủy danh bạ"
                            , Toast.LENGTH_LONG).show();
                }else if(edtName.getEditText().getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Tên danh bạ là bắt buộc", Toast.LENGTH_SHORT).show();
                }
                else {
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    name).build());



                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());


                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());


                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address)
                        .build());


//                    if (company != null) {
//                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                        .build());
//                    }

//                    if(photo != null) {
//                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                            .withValue(ContactsContract.Data.MIMETYPE,
//                                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
//                            .build());
//                    }

                    try {
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        Toast.makeText(getApplicationContext(), "Thêm thành công danh bạ: " + name, Toast.LENGTH_SHORT).show();
                        intent.setAction("android.action.NewContactAct");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Thêm danh bạ thất bại", Toast.LENGTH_LONG).show();
                        Log.e("Error", e.toString());
                    }
                }

            }
            else if(message.equals("CHANGE_CONTACT")){
                if(edtName.getEditText().getText().toString().equals("") && edtPhone.getEditText().getText().toString().equals("")
                        && edtEmail.getEditText().getText().toString().equals("") && edtAddress.getEditText().getText().toString().equals("")){
                    finish();
                    Toast.makeText(getApplicationContext(), "Không có gì để lưu đã hủy danh bạ"
                            , Toast.LENGTH_LONG).show();
                }else if(edtName.getEditText().getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Tên danh bạ là bắt buộc", Toast.LENGTH_SHORT).show();
                }else{
                    ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();
                    //Name
                    ops.add(ContentProviderOperation
                            .newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "=?", new String[] { id
                                    , ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE })
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                            .build());
                    //Phone
                    ops.add(ContentProviderOperation
                            .newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "=?", new String[]{id
                                    , ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE })
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                    //Email
                    ops.add(ContentProviderOperation
                            .newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                                    ContactsContract.Data.MIMETYPE + "=?", new String[]{id
                                    , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE })
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                    ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String []{id})
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address)
                            .build());

                    try{
                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                        Toast.makeText(getApplicationContext(), "Sửa thành công", Toast.LENGTH_SHORT).show();
//                    intent.setAction("android.action.ChangeContactAct");
                        intentChange.putExtra("MESSAGE", "CHANGED");
                        intentChange.putExtra("ID", id);
                        intentChange.putExtra("NAME", name);
                        intentChange.putExtra("PHONE", phone);
                        intentChange.putExtra("EMAIL", email);
                        intentChange.putExtra("ADDRESS", address);
                        startActivity(intentChange);
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("ERROR", "CONTACT:" + e.toString());
                        Toast.makeText(getApplicationContext(), "Thêm danh bạ thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    });
}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            if(requestCode == 1){
//                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
//                ic_create_img.setImageBitmap(bitmap);
//                ByteArrayOutputStream streamy = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, streamy);
//                photo = streamy.toByteArray(); // change bitmap to ByteArray
//            }
//        }
//    }

//    public void takeActionPhoto(){
//        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(takePic.resolveActivity(getPackageManager()) != null){
//            File photoFile = null;
//            photoFile = createPhotoFile();
//            if(photoFile != null){
//                pathToFile = photoFile.getAbsolutePath();
//                Uri photoUrl = FileProvider.getUriForFile(NewContactActivity.this
//                        , "com.example.contact.fileprovider", photoFile);
//                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoUrl);
//                startActivityForResult(takePic, 1);
//            }
//        }
//    }
//    public File createPhotoFile(){
//        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = null;
//        try{
//            image = File.createTempFile(name, ".jpg", storageDir);
//        }catch (IOException e){
//            Log.e("Error: ", e.toString());
//        }
//        return image;
//    }
    //    public void addNewContact(){
//        String DisplayName = "XYZ";
//        String MobileNumber = "123456";
//        String HomeNumber = "1111";
//        String WorkNumber = "2222";
//        String emailID = "email@nomail.com";
//        String company = "bad";
//        String jobTitle = "abcd";
//
//        ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();
//
//        ops.add(ContentProviderOperation.newInsert(
//                ContactsContract.RawContacts.CONTENT_URI)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                .build());
//
//        //------------------------------------------------------ Names
//        if (DisplayName != null) {
//            ops.add(ContentProviderOperation.newInsert(
//                    ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                    .withValue(
//                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
//                            DisplayName).build());
//        }
//
//        //------------------------------------------------------ Mobile Number
//        if (MobileNumber != null) {
//            ops.add(ContentProviderOperation.
//                    newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                    .build());
//        }
//
//        //------------------------------------------------------ Home Numbers
//        if (HomeNumber != null) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                    .build());
//        }
//
//        //------------------------------------------------------ Work Numbers
//        if (WorkNumber != null) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
//                    .build());
//        }
//
//        //------------------------------------------------------ Email
//        if (emailID != null) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
//                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
//                    .build());
//        }
//
//        //------------------------------------------------------ Organization
//        if (!company.equals("") && !jobTitle.equals("")) {
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE,
//                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
//                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                    .build());
//        }
//
//        // Asking the Contact provider to create a new contact
//        try {
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
}
