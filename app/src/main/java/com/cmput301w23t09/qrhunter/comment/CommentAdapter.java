package com.cmput301w23t09.qrhunter.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

  private final Context context;
  private List<Comment> playerComments;

  public CommentAdapter(Context context, List<Comment> playerComments) {
    super(context, 0, playerComments);

    this.context = context;
    this.playerComments = playerComments;
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
    Comment currentComment = playerComments.get(position);

    // set fields of view
    TextView player_name = view.findViewById(R.id.comment_player_name);
    player_name.setText(currentComment.getUsername());

    TextView comment = view.findViewById(R.id.player_comment_input);
    String commentText = currentComment.getComment();

    //shortens comment text if it's too long
    if (commentText.length() > 40) {
      commentText = TextUtils.substring(commentText, 0, 40) + "...";
    }
    comment.setText(commentText);

// set onClickListener to show full comment when clicked
    comment.setOnClickListener(view1 -> {
      showCommentDialog(currentComment.getUsername(), currentComment.getComment());
    });

    player_name.setOnClickListener(view1 -> {
      showCommentDialog(currentComment.getUsername(), currentComment.getComment());
    });

    return view;
  }

  private void showCommentDialog(String playerName, String commentText) {
    // create dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LayoutInflater inflater = LayoutInflater.from(context);
    View dialogView = inflater.inflate(R.layout.comment_dialog, null);

    TextView playerNameTextView = dialogView.findViewById(R.id.comment_dialog_player_name);
    String playerNameHeading = playerName + "'s Comments";
    playerNameTextView.setText(playerNameHeading);

    TextView commentTextView = dialogView.findViewById(R.id.comment_dialog_comment_text);
    commentTextView.setText(commentText);

    builder.setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("Close", (dialog, id) -> dialog.dismiss());

    AlertDialog alert = builder.create();
    alert.show();
  }
}