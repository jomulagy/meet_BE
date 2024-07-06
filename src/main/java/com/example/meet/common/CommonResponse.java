package com.example.meet.common;

import com.example.meet.common.enumulation.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class CommonResponse<T> {
    private String code;
    private String message;
    private T data;

    private CommonResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private static <T> CommonResponse<T> of(String code, String message, T data){
        return new CommonResponse<>(code, message, data);
    }

    public static <T> CommonResponse<T> success(){
        return of("200","success",null);
    }

    public static <T> CommonResponse<T> success(T data){
        return of("200","success",data);
    }

    public static <T> CommonResponse<T> fail(ErrorCode e){ return of(Integer.toString(e.getHttpStatus().value()),e.getMessage(),null);}
    public static <T> CommonResponse<T> fail(HttpStatus status, String message){ return of(status.toString(),message,null);}

}
