package com.example.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contact.adapter.ViewPagerAdapter;
import com.example.contact.fragment.CallFragment;
import com.example.contact.fragment.ContactFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SPEECH_INPUT=1000;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;

    private TextView tvSearch;
    ImageView img_voice_search, ic_scanqr, img_qrcode;
    private FloatingActionButton fab;

    private Dialog dialog_share;

    Uri ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializing vars
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab =(FloatingActionButton)findViewById(R.id.fab);
        img_voice_search = (ImageView) toolbar.findViewById(R.id.ic_voice);
        ic_scanqr = (ImageView) toolbar.findViewById(R.id.ic_scanqr);

        //Dialog share
        dialog_share = new Dialog(MainActivity.this);
        dialog_share.setContentView(R.layout.dialog_share);
        dialog_share.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        img_qrcode = (ImageView) dialog_share.findViewById(R.id.ic_export_qr);


        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Set to toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //Remove Title of Toolbar

        //Add Fragment here
        adapter.addFragment(new CallFragment(),"GẦN ĐÂY");
        adapter.addFragment(new ContactFragment(), "DANH BẠ");
        viewPager.setAdapter(adapter);

        //Custom tablayout
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorHintTextLight), getResources().getColor(R.color.colorTextLight));

        // Event click search btn
        tvSearch = (TextView)findViewById(R.id.btn_Search);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //Speech to Text
        img_voice_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Auto focus to searchview when click image voice
//                sv.setIconified(false);
//                sv.requestFocus();
                speak();
            }
        });

        //fab on click event
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, NewContactActivity.class);
                intent.putExtra("MESSAGE","ADD_CONTACT");
                startActivity(intent);
            }
        });
        //Scan QRCode
        ic_scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan danh bạ");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    public void speak(){
        Intent mIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL
                , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mời bạn nói tên danh bạ");
        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception ex){
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //receive and handle it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult resultScan = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(resultScan != null){
            if(resultScan.getContents()==null){
                Toast.makeText(this, "Đóng trình scan QRCode", Toast.LENGTH_LONG).show();
            }
            else {
//                Toast.makeText(this, resultScan.getContents(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, NewContactActivity.class);
                intent.putExtra("MESSAGE", "ADD_CONTACT_QRCODE");
                intent.putExtra("DATA_QRCODE",resultScan.getContents());
                startActivity(intent);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && null != data){
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to search view
//                    sv.setQuery(result.get(0), true);
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("DATA_SEARCH", result.get(0));
                    startActivity(intent);
                }
                break;
            }
            case 24:{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(MainActivity.this)) {
                        ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this
                                , RingtoneManager.TYPE_RINGTONE, ringtone);
                        Toast.makeText(MainActivity.this, "Đặt nhạc chuông cho máy thành công", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_share:
                TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(MainActivity.TELEPHONY_SERVICE);
                String mPhoneNumber = "";
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    mPhoneNumber = "0" + tMgr.getLine1Number().substring(3);
                }
                String textQR="Bui Huynh Duc,"+mPhoneNumber+",ducbh62cfvn@gmail.com,175 Tay Son - Dong Da - Ha Noi";
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
                return true;
            case R.id.item_setRingtone:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(MainActivity.this)) {
                        Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, ringtone);
                        startActivityForResult(intent , 24);
                    }else {
                        Toast.makeText(MainActivity.this, "Vui lòng cấp quyền để đặt nhạc chuông"
                                , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                return true;
            case R.id.item_send_support:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ducbh62@wru.vn"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Trợ giúp và phản hồi Contact App");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                try{
                    startActivity(intent.createChooser(intent, "Chọn App Email bạn sử dụng"));
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, "Lỗi" + ex, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit")
                .setMessage("Bạn có thực sự muốn thoát ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
