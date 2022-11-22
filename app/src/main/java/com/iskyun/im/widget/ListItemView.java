package com.iskyun.im.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.iskyun.im.R;
import com.iskyun.im.databinding.ViewListItemBinding;

public class ListItemView extends FrameLayout {

    private String title;
    private String subTitle;
    private String content;
    private Drawable icon;
    private Drawable leftIcon;

    private @ColorInt
    int titleColor;
    private @ColorInt
    int subTitleColor;
    private @ColorInt
    int contentColor;

    private float titleSize;
    private float subTitleSize;
    private float contentSize;

    private TextView tvTitle, tvSubTitle, tvContent;
    private ImageView ivIcon,ivLeftIcon;

    private EditText editText;


    public ListItemView(@NonNull Context context) {
        this(context, null);
    }

    public ListItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListItemView);

            title = typedArray.getString(R.styleable.ListItemView_list_item_title);
            subTitle = typedArray.getString(R.styleable.ListItemView_list_item_sub_title);
            content = typedArray.getString(R.styleable.ListItemView_list_item_content);
            icon = typedArray.getDrawable(R.styleable.ListItemView_list_item_icon);
            leftIcon = typedArray.getDrawable(R.styleable.ListItemView_list_item_left_icon);

            titleColor = typedArray.getColor(R.styleable.ListItemView_list_item_title_color,
                    ContextCompat.getColor(context, R.color.colorTitle));
            subTitleColor = typedArray.getColor(R.styleable.ListItemView_list_item_sub_title_color,
                    ContextCompat.getColor(context, R.color.colorSubTitle));
            contentColor = typedArray.getColor(R.styleable.ListItemView_list_item_content_color,
                    ContextCompat.getColor(context, R.color.colorContent));

            titleSize = typedArray.getDimension(R.styleable.ListItemView_list_item_title_size,
                    context.getResources().getDimension(R.dimen.text_size));
            subTitleSize = typedArray.getDimension(R.styleable.ListItemView_list_item_sub_title_size,
                    context.getResources().getDimension(R.dimen.sub_title_size));
            contentSize = typedArray.getDimension(R.styleable.ListItemView_list_item_content_size,
                    context.getResources().getDimension(R.dimen.text_size));

            typedArray.recycle();
        }

        if (icon == null) {
            icon = ContextCompat.getDrawable(context, R.mipmap.icon_arrow_right);
        }
        init(context);
    }


    public void init(Context context) {
        ViewListItemBinding listItemBinding = ViewListItemBinding.inflate(LayoutInflater.from(getContext()));

        this.addView(listItemBinding.getRoot());

        ivIcon = listItemBinding.viewListItemIvIcon;
        tvTitle = listItemBinding.viewListItemTvTitle;
        tvSubTitle = listItemBinding.viewListItemTvSubTitle;
        tvContent = listItemBinding.viewListItemTvContent;
        ivLeftIcon = listItemBinding.viewListItemIvLeftIcon;
        editText = listItemBinding.viewListItemEditContent;
        editText.setVisibility(GONE);

        ivIcon.setImageDrawable(icon);
        ivLeftIcon.setImageDrawable(leftIcon);

        tvTitle.setText(title);
        tvSubTitle.setText(subTitle);
        tvContent.setText(content);

        tvTitle.setTextColor(titleColor);
        tvSubTitle.setTextColor(subTitleColor);
        tvContent.setTextColor(contentColor);

        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTitleSize);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);

    }

    public void updateEditContent(@DrawableRes  int resId, ResultContentChange contentChange){
        tvContent.setVisibility(GONE);
        editText.setVisibility(VISIBLE);
        editText.setText(tvContent.getText());

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setSelection(tvContent.getText().length());
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
        editText.setCursorVisible(true);
        ivIcon.setImageResource(resId);
        ivIcon.setOnClickListener(v -> editText.setText(""));
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                editText.setVisibility(GONE);
                tvContent.setVisibility(VISIBLE);
                tvContent.setText(editText.getText());
                ivIcon.setImageDrawable(icon);
                editText.clearFocus();
                contentChange.contentChange(editText.getText().toString());
            }
        });
    }

    /**
     * 清除焦点
     */
    public void clearFocus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(editText.getFocusable() != NOT_FOCUSABLE)
                editText.clearFocus();
        }else {
            editText.clearFocus();
        }
    }

    public void setImageDrawable(Drawable icon) {
        ivIcon.setImageDrawable(icon);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setSubTitle(String subTitle) {
        tvSubTitle.setText(subTitle);
    }

    public void setContent(String content) {
        tvContent.setText(content);
    }

    public String getText() {
         tvContent.getText();
        return (String) tvContent.getText();
    }

    public String getEditText() {
        editText.getText();
        return  (String)editText.getText().toString();
    }

    public EditText getContentEditText(){
        return this.editText;
    }

    public void setContentColor(@ColorInt int contentColor) {
        this.contentColor = contentColor;
        tvContent.setTextColor(contentColor);
    }

    public interface ResultContentChange{
        void contentChange(String content);
    }
}
