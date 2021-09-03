package cn.roy.zlib.permission_ext;

import java.util.List;

/**
 * @Description: 使用了申请权限注解的类参数
 * @Author: Roy Z
 * @Date: 2021/08/09
 * @Version: v1.0
 */
public class RequestPermissionClassParams {
    private String className;
    private List<RequestPermissionMethodParams> methodParamsList;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<RequestPermissionMethodParams> getMethodParamsList() {
        return methodParamsList;
    }

    public void setMethodParamsList(List<RequestPermissionMethodParams> methodParamsList) {
        this.methodParamsList = methodParamsList;
    }
}
