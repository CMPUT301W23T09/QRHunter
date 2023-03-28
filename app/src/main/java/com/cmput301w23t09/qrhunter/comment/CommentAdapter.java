package com.cmput301w23t09.qrhunter.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.R;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment>{

    private final Context context;
    private List<Comment> players_comments;

    public CommentAdapter(Context context, List<Comment> players_comments) {
        super(context, 0,players_comments);

        this.context = context;
        this.players_comments = players_comments;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the players comment view
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_player_view, parent, false);
        }

        // get player data
        Comment current_comment = players_comments.get(position);

        // set fields of view
        TextView player_name = view.findViewById(R.id.comment_player_name);
        player_name.setText(current_comment.getPlayer().getUsername());

        TextView comment = view.findViewById(R.id.player_comment_input);
        comment.setText(current_comment.getComment());

        return view;
    }


}
