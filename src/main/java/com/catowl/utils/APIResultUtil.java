package com.catowl.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(
    value = "统一响应对象",
    description = "所有接口的统一响应格式"
)
public class APIResultUtil<T> {
    @ApiModelProperty(
        value = "响应码",
        example = "200",
        notes = "200表示成功，其他表示失败"
    )
    private Integer code;

    @ApiModelProperty(
        value = "响应数据",
        notes = "接口返回的具体数据"
    )
    private T data;

    @ApiModelProperty(
        value = "响应信息",
        example = "操作成功",
        notes = "接口返回的提示信息"
    )
    private String msg;

    public static <T> APIResultUtil<T> success(T data) {
        APIResultUtil<T> result = new APIResultUtil<>();
        result.setCode(200);
        result.setData(data);
        result.setMsg("操作成功");
        return result;
    }

    public static <T> APIResultUtil<T> success(T data, String msg) {
        APIResultUtil<T> result = new APIResultUtil<>();
        result.setCode(200);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static <T> APIResultUtil<T> error(String msg) {
        APIResultUtil<T> result = new APIResultUtil<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    public static <T> APIResultUtil<T> error(Integer code, String msg) {
        APIResultUtil<T> result = new APIResultUtil<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
