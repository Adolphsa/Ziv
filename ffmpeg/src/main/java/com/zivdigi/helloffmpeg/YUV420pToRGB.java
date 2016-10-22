package com.zivdigi.helloffmpeg;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by adolph
 * on 2016-10-22.
 */

public class YUV420pToRGB {

    public static void decodeYUV420SPrgb565(int[] rgb, byte[] yuv420sp, int width,
                                            int height) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }


    public static void save(byte[] yuv420, String path, int width, int height,
                            int quality) {
        int[] bytes = new int[width * height * 3];
        decodeYUV420SPrgb565(bytes, yuv420, width, height);
        Bitmap bitmap = Bitmap.createBitmap(bytes, width, height,
                Bitmap.Config.RGB_565);
        File file2 = new File(path + ".jpeg");
        try {
            FileOutputStream out = new FileOutputStream(file2);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                System.out.println("截图在file2");
                out.flush();
                out.close();
            }
        } catch (Exception e) {
        }
    }

    public static void yuv420p_to_yuv420sp(byte[] yuv420p,byte[] yuv420sp,int width,int height)
    {
        //Y
        for(int i=0;i < width*height;i++)
        {
            yuv420sp[i]=yuv420p[i];
        }

        int m=0,n=0;
        for(int j=0;j < width*height/2;j++)
        {
            if(j%2==0)
                yuv420sp[j+width*height]=yuv420p[m++];
            else
                yuv420sp[j+width*height]=yuv420p[n++];
        }

    }
}
