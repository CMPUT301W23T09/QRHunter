package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface LeaderboardAdapterItem<T> {
  View getView(Context context, View convertView, ViewGroup parent, T item);
}
