package za.co.fredkobo.poc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class CaptureSignatureView extends View {

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;
    private float mX;
    private float mY;
    private float TouchTolerance = 4;
    private float LineThickness = 4;

    public CaptureSignatureView(Context context, AttributeSet attr) {
        super(context, attr);
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.argb(255, 0, 0, 0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(LineThickness);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, (h > 0 ? h : ((View) this.getParent()).getHeight()), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private void TouchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void TouchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TouchTolerance || dy >= TouchTolerance) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void TouchUp() {
        if (!path.isEmpty()) {
            path.lineTo(mX, mY);
            canvas.drawPath(path, paint);
        } else {
            canvas.drawPoint(mX, mY, paint);
        }

        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                TouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                TouchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void ClearCanvas() {
        canvas.drawColor(Color.WHITE);
        invalidate();
    }

    public byte[] getBytes() {
        Bitmap b = getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public Bitmap getBitmap() {
        View v = (View) this.getParent();
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        return b;
    }
}
