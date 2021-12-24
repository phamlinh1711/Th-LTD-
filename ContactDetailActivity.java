package com.example.contact;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class ContactDetailActivity extends AppCompatActivity {
    private ImageView btnEdit, btnBack, ic_ringtone, btnExport, img_qrcode;
    private LinearLayout linearLayoutCall, linearLayoutMsg, linearLayoutMail;
    private TextView detail_mobile, detail_personal_email, detail_address;
    String message, id, name, phone, email, address, textQR;
    Dialog dialog_share;
    Uri ringtone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }


        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnEdit = (ImageView) findViewById(R.id.ic_edit);
        ic_ringtone = (ImageView) findViewById(R.id.ic_ringtone);
        btnExport = (ImageView) findViewById(R.id.ic_export_qr);
        detail_mobile = (TextView) findViewById(R.id.detail_mobile);
        detail_personal_email = (TextView) findViewById(R.id.detail_personal_email);
        detail_address = (TextView) findViewById(R.id.detail_address);
        linearLayoutCall = (LinearLayout) findViewById(R.id.detai_call);
        linearLayoutMsg = (LinearLayout) findViewById(R.id.detai_msg);
        linearLayoutMail = (LinearLayout) findViewById(R.id.detai_mail);

        //Dialog share via QRCode
        dialog_share = new Dialog(ContactDetailActivity.this);
        dialog_share.setContentView(R.layout.dialog_share);
        dialog_share.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        img_qrcode = (ImageView) dialog_share.findViewById(R.id.ic_export_qr);

        //get Intent
        message = getIntent().getExtras().getString("MESSAGE");
        id = getIntent().getExtras().getString("ID");
        name = getIntent().getExtras().getString("NAME");
        phone = getIntent().getExtras().getString("PHONE");
        email = getIntent().getExtras().getString("EMAIL");
        address = getIntent().getExtras().getString("ADDRESS");





        toolbar.setTitle(name);
        detail_mobile.setText(phone.equals("")?"Chưa thêm số liên hệ":phone);
        detail_personal_email.setText(email.equals("") ? "Chưa thêm email" : email);
        detail_address.setText(address.equals("") ? "Chưa thêm địa chỉ" : address);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(ContactDetailActivity.this, NewContactActivity.class);
            intent.putExtra("MESSAGE","CHANGE_CONTACT");
            intent.putExtra("ID", id);
            intent.putExtra("NAME", name);
            intent.putExtra("PHONE", phone);
            intent.putExtra("EMAIL", email);
            intent.putExtra("ADDRESS", address);
            startActivity(intent);
            }
        });
        ic_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detail_mobile.getText().equals("Chưa thêm số liên hệ")){
                    Toast.makeText(getApplicationContext(), "Chưa thêm số liên hệ"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(ContactDetailActivity.this)) {

                            Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, ringtone);
                            startActivityForResult(intent , 24);
                        }else {
                            Toast.makeText(ContactDetailActivity.this, "Vui lòng cấp quyền để đặt nhạc chuông"
                                    , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
        linearLayoutCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detail_mobile.getText().toString().equals("Chưa thêm số liên hệ")){
                    Toast.makeText(ContactDetailActivity.this, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detail_mobile.getText().toString())));
                }
            }
        });
        linearLayoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detail_mobile.getText().toString().equals("Chưa thêm số liên hệ")){
                    Toast.makeText(ContactDetailActivity.this, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ detail_mobile.getText().toString())));
                }
            }
        });
        linearLayoutMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detail_personal_email.getText().toString().equals("Chưa thêm email")){
                    Toast.makeText(ContactDetailActivity.this, "Chưa thêm email liên hệ", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("mailto:"));
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{detail_personal_email.getText().toString()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    try{
                        startActivity(intent.createChooser(intent, "Chọn App Email bạn sử dụng"));
                    }catch (Exception ex){
                        Toast.makeText(ContactDetailActivity.this, "Lỗi" + ex, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            textQR= "";
            if(!detail_mobile.getText().toString().equals("Chưa thêm số liên hệ")){
                textQR+=name+","+phone+",";
            }else {
                textQR+=name+","+"null,";
            }
            if(!detail_personal_email.getText().toString().equals("Chưa thêm email")){
                textQR +=email+",";
            }else{
                textQR +="null,";
            }
            if(!detail_address.getText().toString().equals("Chưa thêm địa chỉ")){
                textQR += address;
            }else {
                textQR += "null";
            }
            textQR.trim();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try{
                BitMatrix bitMatrix = multiFormatWriter.encode(textQR, BarcodeFormat.QR_CODE,390,390);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                img_qrcode.setImageBitmap(bitmap);
                dialog_share.show();
            }
            catch (WriterException e){
                e.printStackTrace();
            }
            }
        });
    }

    public void addRingtone(String path){
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phone);
        String []projection = new String[] {ContactsContract.Contacts._ID
                , ContactsContract.Contacts.LOOKUP_KEY};
        Cursor data = getContentResolver().query(lookupUri, projection, null, null, null);
        data.moveToFirst();
        try {
            // Get the contact lookup Uri
            final long contactId = data.getLong(0);
            final String lookupKey = data.getString(1);
            final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
            if (contactUri == null) {
                // Invalid arguments
                return;
            }
            // Apply the custom ringtone
            try{
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, path);
                getContentResolver().update(contactUri, values, null, null);
                Toast.makeText(getApplicationContext(), "Đặt nhạc chuông thành công", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Đặt nhạc chuông thất bại", Toast.LENGTH_SHORT).show();

            }


        } finally {
            // Don't forget to close your Cursor
            data.close();
        }
    }

    @Override
    public void onBackPressed() {
        if(message.equals("SEE_DETAIL")){
            super.onBackPressed();
        }else if(message.equals("CHANGED")){
            startActivity(new Intent(ContactDetailActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 24:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(ContactDetailActivity.this)) {
                            ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            //Check yên lặng
                            if(ringtone == null){
                                addRingtone(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                            }else{
                                addRingtone(ringtone.toString());
                            }
                        }else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    break;

                default:
                    break;
            }
        }
    }
}
