package com.rkumar0206.mymexpenseservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpenseservice.constantsAndEnums.Headers;
import com.rkumar0206.mymexpenseservice.models.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserContextServiceTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    private UserContextService userContextService;

    @BeforeEach
    void setUp() {

        String auth = "Bearer jnvskjbshsbvuysgcbsjhvsvsgcghsvhgcardcHVXJHbkb";

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(auth);

        userContextService = new UserContextService(httpServletRequest);
    }

    @Test
    void getUserInfo_NoAuthHeaderPassed_ExceptionThrown() {

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        assertThatThrownBy(() -> userContextService.getUserInfo())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR);

    }


    @Test
    void getUserInfo_Success() throws JsonProcessingException {

        UserInfo userInfo = new UserInfo(
                "temp user",
                "temp email id",
                UUID.randomUUID().toString(),
                true
        );

        when(httpServletRequest.getHeader(Headers.USER_INFO_HEADER_NAME))
                .thenReturn(new ObjectMapper().writeValueAsString(userInfo));


        UserInfo actual = userContextService.getUserInfo();

        assertEquals(userInfo.getName(), actual.getName());
        assertEquals(userInfo.getEmailId(), actual.getEmailId());
        assertEquals(userInfo.getUid(), actual.getUid());

    }

    @Test
    void getUserInfo_NoUserInfoHeaderPassed_ExceptionThrown() {

        when(httpServletRequest.getHeader(Headers.USER_INFO_HEADER_NAME))
                .thenReturn(null);

        assertThatThrownBy(() -> userContextService.getUserInfo())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR);

    }

    @Test
    void getUserInfo_PartialInfoPassed_ExceptionThrown() throws JsonProcessingException {

        UserInfo userInfo = new UserInfo(
                "",
                "",
                UUID.randomUUID().toString(),
                true
        );

        when(httpServletRequest.getHeader(Headers.USER_INFO_HEADER_NAME))
                .thenReturn(new ObjectMapper().writeValueAsString(userInfo));

        assertThatThrownBy(() -> userContextService.getUserInfo())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorMessageConstants.USER_INFO_NOT_PROPER);

    }


    @Test
    void getUserInfo_JsonParsingExceptionOccurred_ExceptionThrown() {

        when(httpServletRequest.getHeader(Headers.USER_INFO_HEADER_NAME))
                .thenReturn("jssbhbsjhb");

        assertThatThrownBy(() -> userContextService.getUserInfo())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorMessageConstants.USER_INFO_NOT_PROPER);

    }


}