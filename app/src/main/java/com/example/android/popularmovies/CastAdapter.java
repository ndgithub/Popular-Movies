package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Nicky on 11/1/16.
 */

public class CastAdapter extends ArrayAdapter<CastMember> {
    public CastAdapter(Context context, ArrayList<CastMember> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        View listItemView = recycled;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.cast_list_item, parent, false);
        }
        TextView nameView = (TextView) listItemView.findViewById(R.id.real_name);
        TextView charView = (TextView) listItemView.findViewById(R.id.character_name);
        CastMember currentActor = getItem(position);
        nameView.setText(currentActor.getActorName());
        charView.setText(currentActor.getCharacterName());
        ImageView actorPic = (ImageView) listItemView.findViewById(R.id.actor_pic);


        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < 16) {
            actorPic.setBackgroundDrawable(getContext().getDrawable(R.drawable.actor_pic_background_circle));
        } else {
            actorPic.setBackground(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.actor_pic_background_circle));
        }


        Picasso.with(getContext())
                .load("https://image.tmdb.org/t/p/w342" + currentActor.getProfilePicPath())
                .transform(new CircleTransform())
                .resize(275, 0)
                .into(actorPic);

        return listItemView;

    }





    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
