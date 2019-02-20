       /**
         * 实现思路：复制外部储存的so文件到app内部，将app内so的目录通过反射的方式放入nativeLibraryDirectories中。参考了腾讯tinker的相关实现方式。
         * 使用方式：将 ShareReflectUtil、SoUtil、TinkerLoadLibrary放入工程中，在调用so时替换下面代码。
         * 注：so在sd卡中的存放要以 arm64-v8a 、armeabi-v7a、x86 、armeabi 等这些名字作为文件夹的名称,文件夹下面存放so文件
         */

           //path 为sd卡中so文件的存放路径,必须是 arm64-v8a 、armeabi-v7a、x86 、armeabi等这些包含so文件的 [前一级文件夹]
           String path = "";
           File file = new File(path);
           // mContext 为上下文对象
           // "jniLibs" 为复制so到app中的文件夹名称，可不做修改，修改的话将SoUtils 79 行一起修改
           File dir = mContext.getDir("jniLibs", mContext.MODE_PRIVATE);
           //复制外部存储中的so文件到app包内    xxx.so 是so文件的全名
           SoUtils.copySoLib(mContext,file,"xxx.so");
           //判断复制so文件是否成功,成功后加载so
           if(SoUtils.isSuccess){
               try {
                   TinkerLoadLibrary.installNativeLibraryPath(this.getClassLoader(), dir);
                   //xx为so文件去掉lib前缀后的名字
                   System.loadLibrary("xx");
               } catch (Throwable throwable) {

               }
           }
