加载Hello.xlass的Java代码实现如下

```java

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

```

代码逻辑比较简单，首先用字节流方式读取Hello.xlass文件，然后将字节流中每一个数据通过255-val方式还原成原始的字节码字节流，再调用父类类加载器的defineClass生成Class对象即可
main函数中获取到Hello类的反射对象之后，创建对象并反射调用其Hello方法，正确打印如下，证明整个类加载过程OK

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E4%BD%9C%E4%B8%9A2%E8%BF%90%E8%A1%8C%E6%88%AA%E5%9B%BE.png" width="680" height="80%" />


