package com.example.contact;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.contact.adapter.RecyclerViewAdapterContact;
import com.example.contact.fragment.ContactFragment;
import com.example.contact.models.Contact;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    ArrayList<Contact> lstContact;
    RecyclerViewAdapterContact adapter;
    ImageView btn_Back, ic_voice;
    EditText editText;
    public static final int REQUEST_CODE_SPEECH_INPUT=1000;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        EditText et_search = (EditText)findViewById(R.id.et_search);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lstContact = new ArrayList<>();
        loadContacList();

        // Recycler view set adapter
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv_search);
        adapter = new RecyclerViewAdapterContact( this,lstContact);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        //btn back
        btn_Back = (ImageView) findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editText = (EditText)findViewById(R.id.et_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        ic_voice = (ImageView) findViewById(R.id.ic_voice);
        ic_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Auto focus to searchview when click image voice
//                sv.setIconified(false);
//                sv.requestFocus();
                speak();
            }
        });

        //Nếu có intent bắt giọng nói từ bên Main
        if(getIntent().getExtras() != null){
            String rq_main = getIntent().getExtras().getString("DATA_SEARCH");
            editText.setText(rq_main);
        }
    }
    //Hàm bắt giọng nói
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
    //filter
    private void filter(String text){
        ArrayList<Contact> lst = new ArrayList<>();
        for(Contact item : lstContact){
            if(removeAccent(item.getName().toLowerCase()).contains(removeAccent(text).toLowerCase())){
                lst.add(item);
            }
        }
        adapter.updateList(lst);
    }

    // Xóa dấu
    public String removeAccent(String str) {
        str = str.trim();
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d")
                .replaceAll("ê", "e").replaceAll("ă", "â")
                .replaceAll("ư", "u").replaceAll("ô", "o")
                .replaceAll("ơ", "o");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && null != data){
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to search view
                    editText.setText(result.get(0));
                }
            }
            break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void loadContacList(){
        //Accessing to contact list and get info
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            List<String> lstPhoneNumber = new ArrayList<>();
            List<String> lstEmail = new ArrayList<>();

            //Phone
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    , null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                    , new String[]{id}, null);

            while (phoneCursor.moveToNext() && phoneCursor != null){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                lstPhoneNumber.add(phoneNumber);
            }
            phoneCursor.close();

            //Email
            Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI
                    , null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?"
                    , new String[]{id}, null);
            while (emailCursor.moveToNext() && emailCursor != null){
                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                lstEmail.add(email);
            }
            emailCursor.close();

            String address = "";

            Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            Cursor postal_cursor  = getApplicationContext().getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+id, null,null);
            while(postal_cursor.moveToNext())
            {
//                String Strt = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.STREET));
                address = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//                String cntry = postal_cursor.getString(postal_cursor.getColumnIndex(StructuredPostal.COUNTRY));
            }
            postal_cursor.close();

            lstContact.add(new Contact(id, name, name.substring(0, 1).toUpperCase()
                    , lstPhoneNumber.size() == 0 ? "" : lstPhoneNumber.get(0)
                    , lstEmail.size()== 0 ? "" : lstEmail.get(0), address));
        }
        cursor.close();
        Collections.sort(lstContact, new SearchActivity.CustomComparaterLetterContact());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    //Class to Compare lstContact for letters
    private class CustomComparaterLetterContact implements Comparator<Contact> {

        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
