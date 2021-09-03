package cn.roy.zlib.permission_ext;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/08/04
 * @Version: v1.0
 */
public class PermissionHelper {
    private static ConcurrentHashMap<Object, RequestPermissionContextHolder> registerMap = new ConcurrentHashMap<>();

    public static void register(Object obj, Context context) {
        RequestPermissionContextHolder target = null;
        String clsName = obj.getClass().getName();
        try {
            Class<?> clazz = obj.getClass().getClassLoader()
                    .loadClass(clsName + "_RequestPermissionExt");
            Constructor<? extends RequestPermissionContextHolder> constructor =
                    (Constructor<? extends RequestPermissionContextHolder>) clazz.getConstructor();
            target = constructor.newInstance();
            target.setContext(context);
            registerMap.put(obj, target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Object get(Object obj) {
        return registerMap.get(obj);
    }

    public static void unRegister(Object obj) {
        registerMap.remove(obj);
    }

    public static boolean hasPermission(Context context, String[] permissions) {
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_DENIED) {
                allGranted = false;
                break;
            }
        }
        return allGranted;
    }

}
