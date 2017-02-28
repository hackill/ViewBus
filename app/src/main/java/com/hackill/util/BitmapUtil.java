package com.hackill.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

/***
 * bitmap相关工具类
 *
 * @tip 更多方法可以查看 common jar包BitmapUtil.java类
 */
public class BitmapUtil {

    /**
     * convert Bitmap to byte array
     */
    public static byte[] bitmapToByte(Bitmap b) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * convert Drawable to Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    /***
     * 缩放适应宽和高，使之不拉伸的情况下填充
     *
     * @return
     */
    public static Bitmap scaleImageFitToSize(Bitmap org, int newWidth, int newHeight) {

        Bitmap BitmapOrg = org;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = 1;
        if (scaleHeight > scaleWidth) {
            scale = scaleHeight;
        } else {
            scale = scaleWidth;
        }
        return scaleImage(org, scale, scale);
    }

    /**
     * scale image by width and height
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    public static Intent buildPickIntent(Uri uri, int outputX, int outputY) {
        return buildPickIntent(uri, 1, 1, outputX, outputY);
    }

    public static Intent buildCropIntent(Uri uri, int outputX, int outputY) {
        return buildCropIntent(uri, uri, 1, 1, outputX, outputY, false);
    }

    public static Intent buildPickIntent(Uri uri, int aspectX, int aspectY, int outputX, int outputY) {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", uri);
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", aspectX);// 裁剪框比例
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);// 输出图片大小
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        return intent;
    }


    public static Intent buildCropIntent(Uri uriFrom, Uri uriTo, int aspectX, int aspectY, int outputX, int outputY,
                                         boolean returnData) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uriFrom, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("output", uriTo);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", returnData);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        return intent;
    }

    public static Bitmap createMaxCropBitmap(Bitmap bitMap, boolean needRecycle) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 2048;
        int newHeight = 2048;
        //计算 长宽 比例
        if (width >= height) {
            //缩放 高
            double scale = (height * 1.0) / width;
            newHeight = (int) (newWidth * scale);
        } else {
            //缩放 宽
            double scale = (width * 1.0) / height;
            newWidth = (int) (newHeight * scale);
        }
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
        if (needRecycle) bitMap.recycle();
        return newBitMap;
    }

    public static Bitmap createBitmapAvatarThumbnail(Bitmap bitMap, boolean needRecycle) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 300;
        int newHeight = 300;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
        if (needRecycle) bitMap.recycle();
        return newBitMap;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
        //paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        Bitmap grayImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(grayImg);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] colorArray = {
                1, 0, 0, 0, 85,
                0, 1, 0, 0, 85,
                0, 0, 1, 0, 85,
                0, 0, 0, 1, 0};

        final float invSat = 1;
        final float R = 0.213f * invSat;
        final float G = 0.715f * invSat;
        final float B = 0.072f * invSat;

        colorArray[0] = R;
        colorArray[1] = G;
        colorArray[2] = B;
        colorArray[5] = R;
        colorArray[6] = G;
        colorArray[7] = B;
        colorArray[10] = R;
        colorArray[11] = G;
        colorArray[12] = B;

        colorMatrix.set(colorArray);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return grayImg;
    }

//    public static Bitmap convertToBlackWhite(Bitmap bmp, int brightness) {
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        Bitmap grayImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(grayImg);
//        Paint paint = new Paint();
//        ColorMatrix colorMatrix = new ColorMatrix();
//        float[] colorArray = {
//                1, 0, 0, 0, brightness,
//                0, 1, 0, 0, brightness,
//                0, 0, 1, 0, brightness,
//                0, 0, 0, 1, 0};
//
//        final float invSat = 1;
//        final float R = 0.213f * invSat;
//        final float G = 0.715f * invSat;
//        final float B = 0.072f * invSat;
//
//        colorArray[0] = R;
//        colorArray[1] = G;
//        colorArray[2] = B;
//        colorArray[5] = R;
//        colorArray[6] = G;
//        colorArray[7] = B;
//        colorArray[10] = R;
//        colorArray[11] = G;
//        colorArray[12] = B;
//
//        colorMatrix.set(colorArray);
//
//        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
//                colorMatrix);
//        paint.setColorFilter(colorMatrixFilter);
//        canvas.drawBitmap(bmp, 0, 0, paint);
//        return grayImg;
//    }
}
