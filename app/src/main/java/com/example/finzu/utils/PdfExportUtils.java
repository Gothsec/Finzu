package com.example.finzu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExportUtils {

    public static void exportChartToPdf(Context context, View chartView, String fileName) {
        // 1. transform view to bitmap
        Bitmap bitmap = getBitmapFromView(chartView);

        // 2. create pdf file
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // 3. draw bitmap on the page
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // 4. save pdf file
        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        if (!exportDir.exists()) exportDir.mkdirs();

        File file = new File(exportDir, fileName + ".pdf");

        try {
            FileOutputStream out = new FileOutputStream(file);
            document.writeTo(out);
            document.close();
            Toast.makeText(context, "PDF exportado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al exportar PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
