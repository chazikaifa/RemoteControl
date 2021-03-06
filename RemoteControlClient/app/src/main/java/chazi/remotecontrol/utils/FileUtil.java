package chazi.remotecontrol.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import chazi.remotecontrol.PanelListActivity;
import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;

/**
 * Created by 595056078 on 2017/5/13.
 */

public class FileUtil {

    public static final int STATUS_OK = 0;
    public static final int STATUS_FAIL = 1;
    public static final int STATUS_WAIT = 2;

    public static void savePanelIntoSDCard(final Context context, final Panel panel) {
        File appDir = new File(Environment.getExternalStorageDirectory(), Global.FilePlace);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = panel.getId();
        final File file = new File(appDir, fileName);

        if (file.exists()) {
            new AlertDialog.Builder(context)
                    .setMessage("面板\"" + panel.getName() + "\"已经有导出记录，确认覆盖？")
                    .setTitle("文件冲突")
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean flag = file.delete();
                            if (flag) {
                                try {
                                    flag = file.createNewFile();
                                    if (flag) {
                                        writeToFile(context, file, panel);
                                    } else {
                                        Toast.makeText(context, "创建文件失败！", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "删除文件失败！", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            try {
                boolean flag = file.createNewFile();
                if (flag) {
                    writeToFile(context, file, panel);
                } else {
                    Toast.makeText(context, "创建文件失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void writeToFile(Context context, File file, Panel panel) throws IOException {
        FileWriter fw = new FileWriter(file, true);//创建FileWriter对象，用来写入字符流
        BufferedWriter bw = new BufferedWriter(fw); // 将缓冲对文件的输出
        bw.write(panel.toJson().toString());
        bw.newLine();
        bw.flush(); // 刷新该流的缓冲
        bw.close();
        fw.close();

        Toast.makeText(context, "面板\"" + panel.getName() + "\"导出成功！", Toast.LENGTH_SHORT).show();
    }

    public static int readPanelFromSDCard(Context context, File file, final Handler handler) {
        String content = "";
        final int[] status = {STATUS_OK};

        if (file.isDirectory()) {
            Log.i("readFromSDCard", "The File is directory.");
            status[0] = STATUS_FAIL;
        } else {
            try {
                InputStream in = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                //分行读取
                while ((line = reader.readLine()) != null) {
                    content += line + "\n";
                }
                in.close();
            } catch (java.io.FileNotFoundException e) {
                Log.i("readFromSDCard", "The File doesn't not exist.");
                status[0] = STATUS_FAIL;
            } catch (IOException e) {
                Log.i("readFromSDCard", e.getMessage());
                status[0] = STATUS_FAIL;
            }
        }

        Log.i("readFromSDCard",content);

        try {
            final JSONObject jsonObject = new JSONObject(content);
            String id = jsonObject.getString("id");
            if(RealmDb.isPanelExist(id)){
                status[0] = STATUS_WAIT;
                new AlertDialog.Builder(context)
                        .setTitle("面板冲突")
                        .setMessage("已经存在导入的面板，确认要覆盖?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    new Panel(jsonObject);
                                    Message message = new Message();
                                    message.what = PanelListActivity.IMPORT_COMPLETE;
                                    handler.sendMessage(message);
                                } catch (JSONException e) {
                                    Message message = new Message();
                                    message.what = PanelListActivity.IMPORT_FAIL;
                                    handler.sendMessage(message);
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }else {
                new Panel(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            status[0] = STATUS_FAIL;
        }

        return status[0];
    }

    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
