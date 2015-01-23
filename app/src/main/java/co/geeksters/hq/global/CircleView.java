package co.geeksters.hq.global;

/**
 * Created by soukaina on 20/01/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    Paint paint;
    Path path;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(50, 50, 30, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(300, 300, 200, paint);
        //drawCircle(cx, cy, radius, paint)

    }

}