package com.javacamp.SeparateReadWriteDemoSimple.Aspect;


import com.javacamp.SeparateReadWriteDemoSimple.Dao.OrderDao;
import com.javacamp.SeparateReadWriteDemoSimple.Router.DataSourceRouter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Aspect
public class ReadWriteOperationAspect {
    @Resource(name = "randomRouter")
    private DataSourceRouter router;

    // 拦截所有被ReadOnlyOperation修饰的方法，进行读操作负载均衡
    @Pointcut("@annotation(com.javacamp.SeparateReadWriteDemoSimple.Annotation.ReadOnlyOperation)")
    public void readPoint() {

    }

    // 拦截所有被WriteOperation修饰的方法，动态设置数据源
    @Pointcut("@annotation(com.javacamp.SeparateReadWriteDemoSimple.Annotation.WriteOperation)")
    public void writePoint() {

    }

    @Around("com.javacamp.SeparateReadWriteDemoSimple.Aspect.ReadWriteOperationAspect.readPoint()")
    public Object readOperation(ProceedingJoinPoint pjp) {
        Connection conn = null;
        try {
            conn = router.getReadDataSource().getRoConnection();
            if (conn == null) {
                System.err.println("conn is null");
                for (int i = 0; i < 100; i++)
                    System.err.println("#######################");
                System.exit(-1);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        Object result = operation(pjp, conn);
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    @Around("com.javacamp.SeparateReadWriteDemoSimple.Aspect.ReadWriteOperationAspect.writePoint()")
    public Object writeOperation(ProceedingJoinPoint pjp) {
        Connection conn = null;
        try {
            conn = router.getReadWriteDataSource().getRwConnection();
            if (conn == null) {
                System.err.println("conn is null");
                for (int i = 0; i < 100; i++)
                    System.err.println("#######################");
                System.exit(-1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

        Object result = operation(pjp, conn);
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    private Object operation(ProceedingJoinPoint pjp, Connection reaplaceConn) {
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        Class<?>[] parameterTypes = ((MethodSignature)(pjp.getSignature())).getParameterTypes();

        // 替换参数列表中的Connection类型的参数实例为负载均衡选出来的实例
        for (int i = 0; i < args.length; i++) {
            if (parameterTypes[i] == Connection.class) {
                args[i] = reaplaceConn;
                break;
            }
        }

        Object result = null;
        try {
           result = pjp.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }
}
