package com.dicio.dicio_android.renderer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.dicio.component.AssistanceComponent;
import com.dicio.component.output.views.BaseView;
import com.dicio.component.output.views.DescribedImage;
import com.dicio.component.output.views.Description;
import com.dicio.component.output.views.Header;
import com.dicio.component.output.views.Image;
import com.dicio.dicio_android.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

public class OutputRenderer {
    private static TextView customizeHeader(String text, TextView view) {
        view.setText(text);
        view.setTextSize(view.getContext().getResources().getDimension(R.dimen.outputHeaderTextSize));

        view.setGravity(Gravity.CENTER);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }

    private static TextView customizeDescription(String text, HtmlTextView view) {
        view.setHtmlText(text);
        view.setTextSize(view.getContext().getResources().getDimension(R.dimen.outputDescriptionTextSize));

        view.setGravity(Gravity.CENTER);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return view;
    }

    private static ImageView customizeImage(String source, Image.SourceType sourceType, ImageView view) throws IllegalAccessException, NoSuchFieldException {
        switch (sourceType) {
            case url: case local:
                Picasso.get().load(source).into(view);
                break;
            case other:
                Field idField = R.drawable.class.getDeclaredField(source);
                view.setImageResource(idField.getInt(idField));
                break;
        }

        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setAdjustViewBounds(true);
        return view;
    }
    
    
    private static View renderHeader(Header data, Context context) {
        return customizeHeader(data.getText(), new TextView(context));
    }
    
    private static View renderDescription(Description data, Context context) {
        return customizeDescription(data.getText(), new HtmlTextView(context));
    }
    
    private static View renderImage(final Image data, Context context) throws IllegalAccessException, NoSuchFieldException {
        ImageView result = new ImageView(context);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.onClick();
            }
        });

        result.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return customizeImage(data.getSource(), data.getSourceType(), result);
    }
    
    private static View renderDescribedImage(final DescribedImage data, Context context) throws NoSuchFieldException, IllegalAccessException {
        LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.HORIZONTAL);

        result.setClickable(false);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.onClick();
            }
        });

        ImageView image = customizeImage(data.getImageSource(), data.getImageSourceType(), new ImageView(context));
        image.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
        result.addView(image);

        
        LinearLayout textAndHeaderLayout = new LinearLayout(context);
        textAndHeaderLayout.setOrientation(LinearLayout.VERTICAL);
        textAndHeaderLayout.addView(customizeHeader(data.getHeaderText(), new TextView(context)));
        textAndHeaderLayout.addView(customizeDescription(data.getDescriptionText(), new HtmlTextView(context)));

        textAndHeaderLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f));
        result.addView(textAndHeaderLayout);

        return result;
    }

    
    public static View renderComponentOutput(AssistanceComponent component, Context context) throws NoSuchFieldException, IllegalAccessException {
        LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        result.setDividerDrawable(context.getResources().getDrawable(R.drawable.output_list_inner_divider));

        int padding = (int)context.getResources().getDimension(R.dimen.outputListPadding);
        result.setPadding(padding, padding, padding, padding);

        Drawable wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.rounded_rectangle));
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.cardForeground, typedValue, true);
        DrawableCompat.setTint(wrappedDrawable, context.getResources().getColor(typedValue.resourceId));
        Log.w("COLOR", ""+typedValue.resourceId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            result.setBackground(wrappedDrawable);
        } else {
            result.setBackgroundDrawable(wrappedDrawable);
        }

        List<BaseView> allViews = component.getGraphicalOutput();
        for (BaseView view : allViews) {
            if (view instanceof Header) {
                result.addView(renderHeader((Header) view, context));
            } else if (view instanceof Description) {
                result.addView(renderDescription((Description) view, context));
            } else if (view instanceof Image) {
                result.addView(renderImage((Image) view, context));
            } else if (view instanceof DescribedImage) {
                result.addView(renderDescribedImage((DescribedImage) view, context));
            }
        }

        return result;
    }
}