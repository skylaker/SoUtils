package com.foxit;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SoUtils {
    public boolean isSuccess = false; //复制外部so到app包内是否成功
    private Context mContext;

    /**
     * 将一个SO库复制到指定路径，先检查改SO库是否与当前CPU兼容
     *
     * @param context       上下文对象
     * @param sourceDir     SO库所在目录
     * @param soName        SO库名字
     * @return
     */
    public boolean copySoLib(Context context, File sourceDir, String soName) {

        mContext = context;

        if (Build.VERSION.SDK_INT >= 21) {
            //得到设备支持的所有so类型
            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null) {
                for (String abi : abis) {
                    //判断目标文件夹中有没有设备支持的so类型
                    String name = File.separator + abi + File.separator + soName;
                    File sourceFile = new File(sourceDir, name);
                    if (sourceFile.exists()) {
                        //如果存在支持的so类型,就复制到app内存中
                        isSuccess = copyFile(sourceFile.getAbsolutePath(),soName);
                        break;
                    }
                }
            } else {
                Log.e("SoUtils", "该设备不支持加载so文件");
            }
        } else {

            String name = "lib" + File.separator + Build.CPU_ABI + File.separator + soName;
            File sourceFile = new File(sourceDir, name);

            if (!sourceFile.exists() && Build.CPU_ABI2 != null) {
                name = "lib" + File.separator + Build.CPU_ABI2 + File.separator + soName;
                sourceFile = new File(sourceDir, name);

                if (!sourceFile.exists()) {
                    name = "lib" + File.separator + "armeabi" + File.separator + soName;
                    sourceFile = new File(sourceDir, name);
                }
            }
            if (sourceFile.exists()) {
                isSuccess = copyFile(sourceFile.getAbsolutePath(),soName);
            }
        }

        if (!isSuccess) {
            Log.e("SoUtils", "[copySo] 安装 " + soName + " 失败 : NO_MATCHING_ABIS");
        }

        return true;
    }

    /**
     * 复制过程
     * @param filePath
     * @param soName
     * @return
     */
    public boolean copyFile(String filePath,String soName) {
        boolean copyIsFinish = false;
        File dir = mContext.getDir("jniLibs", mContext.MODE_PRIVATE);

        if(!dir.exists()){
            dir.mkdirs();
        }

        File distFile = new File(dir.getAbsolutePath() + File.separator + soName);

        try {
            File jniFile = new File(filePath);
            FileInputStream is = new FileInputStream(jniFile);
            File file = new File(distFile.getAbsolutePath());
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }
}
