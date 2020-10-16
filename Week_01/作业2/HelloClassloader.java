package Week1.homework;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class HelloClassloader extends ClassLoader {
    private byte[] readClassFile(String path) {
        InputStream is = null;
        try {
            File file = new File(path);
            byte[] read_buf = new byte[(int) file.length()];
            is = new FileInputStream(path);
            int val = 0, idx = 0;

            while (true) {
                try {
                    val = is.read();
                    if (val == -1) {
                        break;
                    }

                    read_buf[idx++] = (byte)val;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return read_buf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] decodeClass() {
        byte[] encode_buf = readClassFile("/Users/grh/Programming/IntelliJ_Projects/JavaCamp/src/Week1/homework/Hello.xlass");
        if (encode_buf == null) {
            return null;
        }

        byte[] decode_buf = new byte[encode_buf.length];
        for (int i = 0; i < decode_buf.length; i++) {
            // 恢复原始的class文件的字节流数据
            decode_buf[i] = (byte) (255 - encode_buf[i]);
        }

        return decode_buf;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = decodeClass();
        if (classData == null) {
            return null;
        }

        return defineClass(name, classData, 0, classData.length);
    }

    public static void main(String[] args) {
        try {
            Object obj = new HelloClassloader().findClass("Hello").newInstance();

            // 反射调用实例的方法
            obj.getClass().getDeclaredMethod("hello").invoke(obj);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
