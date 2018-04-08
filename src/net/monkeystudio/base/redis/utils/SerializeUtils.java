package net.monkeystudio.base.redis.utils;


import java.io.*;

/**
 * 序列化工具
 * @author hebo2
 *
 */
public class SerializeUtils {

    /**
     * 序列化，转为字节数组
     * @param object
     * @return
     */
    public static byte[] serialize(Object object){

        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try{
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();

            baos.close();
            oos.close();
            return bytes;
        }
        catch(IOException e){
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 返序列表，字节数组转为对象
     * @param bytes
     * @return
     */
    public static Object unserialize( byte[] bytes) {

        ByteArrayInputStream bais = null;

        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object obj = ois.readObject();

            bais.close();
            ois.close();

            return obj;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
