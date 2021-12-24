package com.example.contact.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contact.ContactDetailActivity;
import com.example.contact.R;
import com.example.contact.fragment.CallFragment;
import com.example.contact.models.Calls;
import com.example.contact.models.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapterCalls extends RecyclerView.Adapter<RecyclerViewAdapterCalls.MyViewHolder>{

    Context myContext;
    List<Calls> mylst;

    private TextView dialog_tvName, dialog_tvPhone, dialog_Fname;
    Dialog mDialog;
    String id, Name, Phone, Email;

    public RecyclerViewAdapterCalls(Context _context, List<Calls> _lst) {
        this.myContext = _context;
        this.mylst = _lst;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(myContext).inflate(R.layout.item_calls, viewGroup, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);

        //Initializing my dialog
        mDialog = new Dialog(myContext);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewHolder.items_callLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_tvName = mDialog.findViewById(R.id.dialog_tvName);
                dialog_tvPhone = mDialog.findViewById(R.id.dialog_tvPhone);
                dialog_Fname = mDialog.findViewById(R.id.dialog_Fname);

                dialog_tvName.setText(mylst.get(viewHolder.getAdapterPosition()).getName());
                dialog_tvPhone.setText(mylst.get(viewHolder.getAdapterPosition()).getPhone());

                dialog_Fname.setText(mylst.get(viewHolder.getAdapterPosition()).getFname());
//                dialog_contact_img.setImageResource(mData.get(viewHolder.getAdapterPosition()).getPhoto());
//                Toast.makeText(myContext, "Test Click " + String.valueOf(viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();

                mDialog.show();

                //Sự kiện click vào icon gọi để chuyển hướng cuộc gọi đến sđt đc chọn
                ImageView call_button = mDialog.findViewById(R.id.call_button);
                ImageView chat_button = mDialog.findViewById(R.id.chat_button);
                ImageView info_button = (ImageView)mDialog.findViewById(R.id.info_button);

                call_button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (dialog_tvPhone.getText().toString().equals("Chưa thêm số liên hệ")){
                            Toast.makeText(myContext, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                        }else {
                            myContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialog_tvPhone.getText().toString())));
                        }
                    }
                });

                //click vào icon sms để vào ô chat với sđt đó
                chat_button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (dialog_tvPhone.getText().toString().equals("Chưa thêm số liên hệ")){
                            Toast.makeText(myContext, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                        }else {
                            myContext.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ dialog_tvPhone.getText().toString())));
                        }
                    }
                });

                /*//Click vào để xem và thay đổi info contact
                info_button.setOnClickListener(new View.OnClickListener() {
                    String phone = mylst.get(viewHolder.getAdapterPosition()).getPhone();
                    ContentResolver resolver = myContext.getContentResolver();

                    Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                            null, null, null);

                    @Override
                    public void onClick(View v) {
                        while(phoneCursor.moveToNext()){
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if(phoneNumber.equalsIgnoreCase(phone)){
                                Intent intent = new Intent(myContext, ContactDetailActivity.class);
                                intent.putExtra("ID", id);
                                intent.putExtra("NAME", Name);
                                intent.putExtra("PHONE", Phone);
                                intent.putExtra("EMAIL", Email);
                                myContext.startActivity(intent);
                            }
                        }

                    }
                });*/
            }
        });

        viewHolder.items_callLog.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewHolder.items_callLog.setOnClickListener(null);
//                Toast.makeText(myContext, mylst.get(viewHolder.getAdapterPosition()).getId(),Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                builder.setTitle("Delete")
                        .setMessage("Bạn có thực sự muốn xóa danh bạ này ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAt(viewHolder.getAdapterPosition());
                                //DeleteCallLogByNumber(mylst.get(viewHolder.getAdapterPosition()).getId());
                            }
                        })
                        .setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });

        return viewHolder;
    }



    public void removeAt(int position) {
        mylst.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mylst.size());
    }

    public void DeleteCallLogByNumber(String _id) {
        Uri CALLLOG_URI = Uri.parse("content://call_log/calls");
        Cursor cursorLog = myContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        String id = "";
        String number="NUMBER=";
        while (cursorLog.moveToNext()){
            id = cursorLog.getString(cursorLog.getColumnIndex(CallLog.Calls._ID));
            number += cursorLog.getString(cursorLog.getColumnIndex(CallLog.Calls.NUMBER));
            if(id.equalsIgnoreCase(_id)){
                myContext.getContentResolver().delete(CallLog.Calls.CONTENT_URI, number, null);
            }
        }

        Toast.makeText(myContext, "Call Log Deleted", Toast.LENGTH_SHORT).show();
//        CallLog.Calls.CONTENT_URI

    }


    /*Chuyển dữ liệu mới vào item view đã được tái tạo lại (Recycle) */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
        if(i != 0) {
            Calls currentCall = mylst.get(i);
            Calls prevCall = mylst.get(i - 1);

            Date currentDate = Calendar.getInstance().getTime();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
            String date = dateFormat.format(currentDate);

            if (!dateFormat.format(currentCall.getDateraw()).equalsIgnoreCase(dateFormat.format(prevCall.getDateraw())))  {
                myViewHolder.sort.setVisibility(View.VISIBLE);
                // set nhãn
                myViewHolder.name_label.setText(setContentSortLabel(mylst.get(i).getDateraw()));

                myViewHolder.tvName.setText(mylst.get(i).getName());
                myViewHolder.tvFname.setText(mylst.get(i).getFname());
                myViewHolder.tvDuration.setText(mylst.get(i).getDuration());
                myViewHolder.tvDate.setText(mylst.get(i).getDate());
            }
            else {
                myViewHolder.sort.setVisibility(View.GONE);

                myViewHolder.tvName.setText(mylst.get(i).getName());
                myViewHolder.tvFname.setText(mylst.get(i).getFname());
                myViewHolder.tvDuration.setText(mylst.get(i).getDuration());
                myViewHolder.tvDate.setText(mylst.get(i).getDate());
            }
        }else {
            myViewHolder.sort.setVisibility(View.VISIBLE);
            myViewHolder.name_label.setText(setContentSortLabel(mylst.get(i).getDateraw()));

            myViewHolder.tvName.setText(mylst.get(i).getName());
            myViewHolder.tvFname.setText(mylst.get(i).getFname());
            myViewHolder.tvDuration.setText(mylst.get(i).getDuration());
            myViewHolder.tvDate.setText(mylst.get(i).getDate());

//            Log.i("first name = ", mylst.get(i).getFname());
        }
    }

    public void ColectionCalls(List<Calls> lst){
        int count = 0;
        for(int i = 0; i<lst.size(); i++){
        }
    }

    //  Tạo nhãn
    public static String setContentSortLabel(Date date_item){
        String contentLabel = "";
        Date currentDate = Calendar.getInstance().getTime();

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar2.add(Calendar.DATE, -2);
        Date OneDayAgo = calendar1.getTime();
        Date TwoDayAgo = calendar2.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM");
        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("d 'Tháng 'M',' yyyy  ");

        String date1 = dateFormat.format(OneDayAgo);
        String date2 = dateFormat.format(TwoDayAgo);
        String today = dateFormat.format(currentDate);

        if(dateFormat.format(date_item).equalsIgnoreCase(today)){
            return contentLabel = "Hôm nay ";
        }else {
            if (dateFormat.format(date_item).equalsIgnoreCase(date1)) {
                return contentLabel = "Hôm qua ";
            }else
                return contentLabel = dateFormatOutput.format(date_item);
        }
    }

    //update list
    public void uploadCallsLog(List<Calls> newlst){
        mylst = newlst;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mylst.size();
    }

    public String getPhoneNumber(int position){
        return mylst.get(position).getPhone();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout items_callLog;
        private TextView tvName;
        private TextView tvFname;
        private ImageView img;
        private TextView tvDuration;
        private TextView tvDate;

        private LinearLayout sort;
        private TextView name_label;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            items_callLog =itemView.findViewById(R.id.items_callLog);
            tvName = itemView.findViewById(R.id.tvName);
            tvFname = itemView.findViewById(R.id.tvFname);
            img = itemView.findViewById(R.id.img_contact);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDate = itemView.findViewById(R.id.tvDate);

            sort = itemView.findViewById(R.id.sort_label);
            name_label = itemView.findViewById(R.id.tv_name_label);

        }
    }
}
