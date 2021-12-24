package com.example.contact.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contact.ContactDetailActivity;
import com.example.contact.models.Contact;
import com.example.contact.R;

import java.util.ArrayList;

public class RecyclerViewAdapterContact extends RecyclerView.Adapter<RecyclerViewAdapterContact.MyViewHolder> {
    Context mContext;
    ArrayList<Contact> mData;
    Dialog mDialog;
    private TextView dialog_tvName, dialog_tvPhone, dialog_Fname;
    String id, Name, Phone, Email, Address;
    public RecyclerViewAdapterContact(Context mContext, ArrayList<Contact> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.item_contact, viewGroup, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);
        //Initializing my dialog
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_contact);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewHolder.items_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_tvName = (TextView)mDialog.findViewById(R.id.dialog_tvName);
                dialog_tvPhone = (TextView)mDialog.findViewById(R.id.dialog_tvPhone);
                dialog_Fname = (TextView)mDialog.findViewById(R.id.dialog_Fname);
//                ImageView dialog_contact_img = (ImageView) mDialog.findViewById(R.id.dialog_img);
                id = mData.get(viewHolder.getAdapterPosition()).getId();
                Name = mData.get(viewHolder.getAdapterPosition()).getName();
                Phone = mData.get(viewHolder.getAdapterPosition()).getPhone();
                Email = mData.get(viewHolder.getAdapterPosition()).getEmail();
                Address = mData.get(viewHolder.getAdapterPosition()).getAddress();

                dialog_tvName.setText(mData.get(viewHolder.getAdapterPosition()).getName());
                dialog_tvPhone.setText(mData.get(viewHolder.getAdapterPosition()).getPhone()
                .equals("") ? "Chưa thêm số liên hệ" : mData.get(viewHolder.getAdapterPosition()).getPhone());
                dialog_Fname.setText(mData.get(viewHolder.getAdapterPosition()).getFname());
//                dialog_contact_img.setImageResource(mData.get(viewHolder.getAdapterPosition()).getPhoto());
//                Toast.makeText(mContext, "Test Click " + String.valueOf(viewHolder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                mDialog.show();

                //Sự kiện click vào icon gọi để chuyển hướng cuộc gọi đến sđt đc chọn
                ImageView call_button = (ImageView)mDialog.findViewById(R.id.call_button);
                ImageView chat_button = (ImageView)mDialog.findViewById(R.id.chat_button);
                ImageView info_button = (ImageView)mDialog.findViewById(R.id.info_button);
                call_button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    if (dialog_tvPhone.getText().toString().equals("Chưa thêm số liên hệ")){
                        Toast.makeText(mContext, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                    }else {
                        mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialog_tvPhone.getText().toString())));
                    }
                    }
                });
                //click vào icon sms để vào ô chat với sđt đó
                chat_button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    if (dialog_tvPhone.getText().toString().equals("Chưa thêm số liên hệ")){
                        Toast.makeText(mContext, "Chưa thêm số liên hệ", Toast.LENGTH_SHORT).show();
                    }else {
                        mContext.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ dialog_tvPhone.getText().toString())));
                    }
                    }
                });
                //Click vào để xem và thay đổi info contact
                info_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent intent = new Intent(mContext, ContactDetailActivity.class);
                    intent.putExtra("MESSAGE", "SEE_DETAIL");
                    intent.putExtra("ID", id);
                    intent.putExtra("NAME", Name);
                    intent.putExtra("PHONE", Phone);
                    intent.putExtra("EMAIL", Email);
                    intent.putExtra("ADDRESS", Address);
                    mContext.startActivity(intent);
                    }
                });
            }
        });
        viewHolder.items_contact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                viewHolder.items_contact.setOnClickListener(null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete")
                        .setMessage("Bạn có thực sự muốn xóa danh bạ này ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAtDB(mData.get(viewHolder.getAdapterPosition()).getId());
                                removeAt(viewHolder.getAdapterPosition());
                                if(mData.size() == 0){
                                    Toast.makeText(mContext, "Danh sách danh bạ rỗng", Toast.LENGTH_SHORT).show();
                                }
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
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void removeAtDB(String ID){
        ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();
        ops.add(ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = ?",
                        new String[] { ID })
                .build());
        try {
            mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Lỗi " + e, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        if(position != 0) {
            Contact currentContact = mData.get(position);
            Contact prevContact = mData.get(position - 1);
            if (!currentContact.getFname().equals(prevContact.getFname())) {
                myViewHolder.ln_cover.setVisibility(View.VISIBLE);
                myViewHolder.pst_tv.setText(mData.get(position).getFname());
                myViewHolder.tvName.setText(mData.get(position).getName());
                myViewHolder.tvFname.setText(mData.get(position).getFname());
            }
            else {
                myViewHolder.ln_cover.setVisibility(View.GONE);
                myViewHolder.tvName.setText(mData.get(position).getName());
                myViewHolder.tvFname.setText(mData.get(position).getFname());
            }
        }else {
            myViewHolder.ln_cover.setVisibility(View.VISIBLE);
            myViewHolder.pst_tv.setText(mData.get(position).getFname());
            myViewHolder.tvName.setText(mData.get(position).getName());
            myViewHolder.tvFname.setText(mData.get(position).getFname());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void updateList(ArrayList<Contact> lstNew){
        mData = lstNew;
        notifyDataSetChanged();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout items_contact;
        private LinearLayout ln_cover;
        private TextView tvName;
        private TextView tvFname;
        private TextView pst_tv;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            items_contact = (LinearLayout) itemView.findViewById(R.id.contact_items_id);
            ln_cover = (LinearLayout) itemView.findViewById(R.id.ln_cover);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            tvFname = (TextView)itemView.findViewById(R.id.tvFname);
            pst_tv = (TextView)itemView.findViewById(R.id.pst_tv);
            img = (ImageView)itemView.findViewById(R.id.img_contact);
        }
    }
}
