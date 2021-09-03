package cn.roy.zlib.permission_ext;

import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * @Description: 使用了申请权限注解的方法参数
 * @Author: Roy Z
 * @Date: 2021/08/09
 * @Version: v1.0
 */
public class RequestPermissionMethodParams {
    private RequestPermissionParams annotationParams;
    private String methodName;
    private TypeMirror returnType;
    private List<MethodParameter> parameterList;

    public RequestPermissionParams getAnnotationParams() {
        return annotationParams;
    }

    public void setAnnotationParams(RequestPermissionParams annotationParams) {
        this.annotationParams = annotationParams;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public TypeMirror getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeMirror returnType) {
        this.returnType = returnType;
    }

    public List<MethodParameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<MethodParameter> parameterList) {
        this.parameterList = parameterList;
    }

    public static class MethodParameter {
        private TypeMirror type;
        private String name;

        public TypeMirror getType() {
            return type;
        }

        public void setType(TypeMirror type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
