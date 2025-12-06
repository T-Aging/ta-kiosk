package com.example.tak.modules.kiosk.login.service;

import com.example.tak.modules.kiosk.login.client.CentralAuthClient;
import com.example.tak.modules.kiosk.login.dto.request.KioskPhoneNumLoginCentRequest;
import com.example.tak.modules.kiosk.login.dto.request.PhoneNumLoginRequest;
import com.example.tak.modules.kiosk.login.dto.response.KioskPhoneNumLoginCentResponse;
import com.example.tak.modules.kiosk.login.dto.response.PhoneNumLoginResponse;
import com.example.tak.modules.kiosk.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final CentralAuthClient centralAuthClient;
    private final WebSocketSessionManager webSocketSessionManager;

    @Override
    public PhoneNumLoginResponse loginByPhoneNum(PhoneNumLoginRequest request) {
        // 1) 회원용 모바일 앱 서버에 보낼 DTO
        KioskPhoneNumLoginCentRequest centRequest = new KioskPhoneNumLoginCentRequest();
        centRequest.setPhoneNumber(request.getPhoneNumber());

        // 2) 중앙 인증 서버 호출
        KioskPhoneNumLoginCentResponse centResponse = centralAuthClient.loginByPhoneNum(centRequest);

//        // ---------
//        KioskPhoneNumLoginCentResponse centResponse;
//        try {
//            centResponse = centralAuthClient.loginByPhoneNum(centRequest);
//        } catch (Exception e) {
//            return PhoneNumLoginResponse.builder()
//                    .login_success(false)
//                    .message("CENTRAL_SERVER_ERROR")
//                    .build();
//        }
//
//        // ---------

        if(centResponse == null){
            return PhoneNumLoginResponse.builder()
                    .login_success(false)
                    .message("CENTRAL_SERVER_ERROR")
                    .build();
        }



        // 3) 응답 채우기
        PhoneNumLoginResponse.PhoneNumLoginResponseBuilder builder
                = PhoneNumLoginResponse.builder()
                .login_success(centResponse.isLogin_success())
                .message(centResponse.getMessage())
                .userId(centResponse.getUserId())
                .username(centResponse.getUsername())
                .maskedPhone(centResponse.getMaskedPhone());

        // 4) 로그인 성공 시 websocket 세션에 userID 바인딩 함
        if(centResponse.isLogin_success() && centResponse.getUserId() != null){
            webSocketSessionManager.attachUser(
                    request.getWebSocketSessionId(),
                    centResponse.getUserId()
            );
        }

        return builder.build();
    }
}
