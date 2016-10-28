package cn.edu.nini.miniweather;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nini on 2016/10/27.
 */
public class LetterIndexView extends View {
    //当前手指滑动到的位置
    private int choosedPosition = -1;
    //画文字的画笔
    private Paint paint;
    //右边的所有文字
    private String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    //页面正中央的TextView，用来显示手指当前滑动到的位置的文本
    private TextView textViewDialog;
    //接口变量，该接口主要用来实现当手指在右边的滑动控件上滑动时ListView能够跟着滚动
    private UpdateListView updateListView;

    public LetterIndexView(Context context) {
        this(context, null);
    }

    public LetterIndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(45);
    }

    public void setTextViewDialog(TextView textViewDialog) {
        this.textViewDialog = textViewDialog;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int perTextHeight = (getHeight()-300) / letters.length;
        for (int i = 0; i < letters.length; i++) {
            if (i == choosedPosition) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.BLACK);
            }
            canvas.drawText(letters[i], (getWidth() - paint.measureText(letters[i])) / 2, 300+(i + 1) * perTextHeight, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int perTextHeight = (getHeight()-300) / letters.length;
        float y = event.getY()-300;
        //找到当前手指点击位置的字母
        int currentPosition = (int) (y / perTextHeight);
        String letter = letters[currentPosition];
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                if (textViewDialog != null) {
                    textViewDialog.setVisibility(View.GONE);
                }
                break;
            default:
                setBackgroundColor(Color.TRANSPARENT);
                if (currentPosition > -1 && currentPosition < letters.length) {
                    if (textViewDialog != null) {
                        textViewDialog.setVisibility(View.VISIBLE);
                        textViewDialog.setText(letter);
                    }
                    if (updateListView != null) {
                        //内部执行用户重写的方法
                        updateListView.updateListView(letter);
                    }
                    choosedPosition = currentPosition;
                }
                break;
        }
        invalidate();
        return true;
    }

    public void setUpdateListView(UpdateListView updateListView) {
        this.updateListView = updateListView;
    }

    public interface UpdateListView {
        public void updateListView(String currentChar);
    }

    public void updateLetterIndexView(int currentChar) {
        for (int i = 0; i < letters.length; i++) {
            if (currentChar == letters[i].charAt(0)) {
                //由listview的滑动改变letterview的UI更新
                choosedPosition = i;
                invalidate();
                break;
            }
        }
    }
}