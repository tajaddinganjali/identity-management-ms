package com.management.register.controller;

import com.management.config.security.UserPrincipial;
import com.management.register.dto.ApiResponseDTO;
import com.management.register.dto.RegisterPinRequestDTO;
import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.dto.SetPasscodeRequestDTO;
import com.management.register.dto.SetPasscodeResponseDTO;
import com.management.register.dto.internal.HeaderInfoDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import com.management.register.service.RegisterService;
import com.management.util.constant.PropertyConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
@Validated
@Api("Service for user registration operations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegisterService registerService;

    @PostMapping("/pin")
    @ApiOperation(value = "Register with PIN",
            response = RegisterPinResponseDTO.class,
            httpMethod = "POST")
    public ApiResponseDTO<RegisterPinResponseDTO> registerWithPin(
            @RequestHeader(value = "accept-language", defaultValue = "az") String lang,
            @RequestHeader(value = "X-App-Version", required = false) String appVersion,
            @Valid @RequestBody RegisterPinRequestDTO registerPinRequest) {

        return ApiResponseDTO.<RegisterPinResponseDTO>builder()
                .data(registerService.registerWithPin(RegisterRequisiteDTO.builder()
                        .headerInfo(HeaderInfoDTO.builder()
                                .appVersion(appVersion)
                                .lang(lang)
                                .build())
                        .registerPinRequest(registerPinRequest)
                        .build()))
                .message(PropertyConstants.SUCCESS)
                .build();
    }

    @PostMapping("/setpasscode")
    @ApiOperation(value = "Set user's passcode.",
            response = SetPasscodeResponseDTO.class,
            httpMethod = "POST")
    public ApiResponseDTO<SetPasscodeResponseDTO> setPassCode(UserPrincipial userPrincipial,
                                                              @RequestHeader("Authorization")
                                                                      String authorizationHeader,
                                                              @RequestHeader(value = "accept-language",
                                                                      defaultValue = "az") String lang,
                                                              @Valid @RequestBody
                                                                      SetPasscodeRequestDTO setPasscodeRequestDTO)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        return ApiResponseDTO.<SetPasscodeResponseDTO>builder()
                .data(registerService.setPasscode(userPrincipial, setPasscodeRequestDTO, authorizationHeader, lang))
                .message(PropertyConstants.SUCCESS)
                .build();
    }

}
