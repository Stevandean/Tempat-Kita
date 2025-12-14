package com.example.tempatkita.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tempatkita.R;
import com.example.tempatkita.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.VH> {

    private Context context;
    private List<Comment> list;

    // ===== REPLY STATE =====
    private String replyingToId = null;

    public CommentAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.list = list;
    }

    // 🔥 dipanggil DetailActivity
    public String getReplyingToId() {
        return replyingToId;
    }

    public void clearReply() {
        replyingToId = null;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Comment c = list.get(i);

        h.txtUser.setText(c.getUserName());
        h.txtContent.setText(c.getContent());

        // ===== INDENT REPLY =====
        ViewGroup.MarginLayoutParams p =
                (ViewGroup.MarginLayoutParams) h.itemView.getLayoutParams();

        if (c.getParentId() != null) {
            p.leftMargin = 64; // reply menjorok
        } else {
            p.leftMargin = 0;
        }
        h.itemView.setLayoutParams(p);

        // ===== CLICK REPLY =====
        h.btnReply.setOnClickListener(v -> {
            replyingToId = c.getId();
            Toast.makeText(context,
                    "Membalas " + c.getUserName(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView txtUser, txtContent, btnReply;

        VH(View v) {
            super(v);
            txtUser = v.findViewById(R.id.txtUser);
            txtContent = v.findViewById(R.id.txtContent);
            btnReply = v.findViewById(R.id.btnReply);
        }
    }
}
