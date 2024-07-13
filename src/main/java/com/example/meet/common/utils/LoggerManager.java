package com.example.meet.common.utils;

import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerManager {

    private final JavaMailSender mailSender;

    public void logError(HttpServletRequest request, Exception ex){
        String requestUrl = request.getRequestURL().toString();
        String requestMethod = request.getMethod();
        String queryString = request.getQueryString();
        String requestBody = extractRequestBody(request);
        String className = ex.getStackTrace()[0].getClassName();
        String methodName = extractMethodName(ex.getStackTrace()[0].getMethodName()) + " (Line " + ex.getStackTrace()[0].getLineNumber() + ")";
        String errorMessage = ex.getMessage();
        String stackTrace = getStackTraceAsString(ex);

        // 이메일 전송
        sendErrorEmail(requestUrl, requestMethod, queryString, requestBody, className, methodName, errorMessage, stackTrace);

    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private String extractMethodName(String lambdaMethodName) {
        if (lambdaMethodName.startsWith("lambda$") && lambdaMethodName.contains("$")) {
            return lambdaMethodName.substring(lambdaMethodName.indexOf('$') + 1, lambdaMethodName.lastIndexOf('$'));
        }
        return lambdaMethodName;
    }

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            }
        }
        return "{}";
    }

    private void sendErrorEmail(String requestUrl, String requestMethod, String queryString, String requestBody, String className, String methodName, String errorMessage, String stackTrace)
    {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("jomulagy988@gmail.com");
            helper.setSubject("[Meet Application] Error Notification");
            String htmlMsg = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/error-email-template.html")), StandardCharsets.UTF_8);

            htmlMsg = htmlMsg.replace("{{requestUrl}}", requestUrl)
                    .replace("{{requestMethod}}", requestMethod)
                    .replace("{{queryString}}", queryString != null ? queryString : "null")
                    .replace("{{requestBody}}", requestBody)
                    .replace("{{className}}", className)
                    .replace("{{methodName}}", methodName)
                    .replace("{{errorMessage}}", errorMessage)
                    .replace("{{stackTrace}}", stackTrace);

            helper.setText(htmlMsg, true); // true indicates HTML

            mailSender.send(message);
        } catch (MessagingException | IOException e) {
            throw new BusinessException(ErrorCode.MAIL_SEND_ERROR);
        }


    }
}
