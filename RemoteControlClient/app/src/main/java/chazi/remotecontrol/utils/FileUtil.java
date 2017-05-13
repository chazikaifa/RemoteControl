package chazi.remotecontrol.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import chazi.remotecontrol.entity.Panel;

/**
 * Created by 595056078 on 2017/5/13.
 */

public class FileUtil {
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
                            if(flag){
                                try {
                                    flag = file.createNewFile();
                                    if(flag){
                                        writeToFile(context,file,panel);
                                    }else {
                                        Toast.makeText(context,"创建文件失败！",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(context,"删除文件失败！",Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else {
            try {
                boolean flag = file.createNewFile();
                if(flag){
                    writeToFile(context,file,panel);
                }else {
                    Toast.makeText(context,"创建文件失败！",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void writeToFile(Context context,File file,Panel panel) throws IOException {
        FileWriter fw = new FileWriter(file, true);//创建FileWriter对象，用来写入字符流
        BufferedWriter bw = new BufferedWriter(fw); // 将缓冲对文件的输出
        bw.write(panel.toJson().toString());
        bw.newLine();
        bw.flush(); // 刷新该流的缓冲
        bw.close();
        fw.close();

        Toast.makeText(context,"面板\""+panel.getName()+"\"导出成功！",Toast.LENGTH_SHORT).show();
    }
}
